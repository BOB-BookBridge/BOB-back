package com.bob.statistics.application;

import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.post.domain.Post;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.status.Status;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore.EntityState;
import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 스냅샷 기록 처리 테스트")
class StatisticsSnapshotRecordServiceRecordTest {

    @InjectMocks
    private StatisticsSnapshotRecordService service;

    @Mock
    private StatisticsMetricStore metricStore;

    @Mock
    private StatisticsEntityStateStore entityStateStore;

    @Mock
    private StatisticsCohortCurrentSnapshotService cohortCurrentSnapshotService;

    @Test
    void 당일_생성_이벤트는_레디스_저장소에_기록한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(Status.REQUESTED);
        ReflectionTestUtils.setField(trade, "id", 1L);
        ReflectionTestUtils.setField(trade, "createdAt", txStartedAt.plusSeconds(1));

        service.record(trade, txStartedAt, false);

        then(metricStore).should().saveAll(anyList(), eq(txStartedAt));
        then(cohortCurrentSnapshotService).should(never()).apply(anyList());
    }

    @Test
    void 과거_생성_이벤트는_생성일_기준_집계로_전달한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(Status.COMPLETED);
        ReflectionTestUtils.setField(trade, "id", 2L);
        ReflectionTestUtils.setField(trade, "createdAt", txStartedAt.minusDays(1));

        service.record(trade, txStartedAt, false);

        then(metricStore).should(never()).saveAll(anyList(), eq(txStartedAt));
        then(cohortCurrentSnapshotService).should().apply(anyList());
    }

    @Test
    void 당일_게시글_상태전이는_이전상태를_감산하고_신규상태를_가산해_저장한다() {
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 52);
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 10L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusHours(1));
        post.deactivate();

        given(entityStateStore.read("post", "10"))
            .willReturn(Optional.of(new EntityState("post", "10", txStartedAt.toLocalDate(), "BANNED", true)));

        service.record(post, txStartedAt, false);

        ArgumentCaptor<List<StatisticsMetricEvent>> captor = ArgumentCaptor.forClass(List.class);
        then(metricStore).should().saveAll(captor.capture(), eq(txStartedAt));
        List<StatisticsMetricEvent> stored = captor.getValue();

        assertThat(stored)
            .extracting(StatisticsMetricEvent::metric, StatisticsMetricEvent::value)
            .containsExactlyInAnyOrder(
                org.assertj.core.groups.Tuple.tuple("banned_posts", -1L),
                org.assertj.core.groups.Tuple.tuple("deleted_posts", 1L)
            );
    }

    @Test
    void 당일_게시글_상태가_기존과_같으면_저장하지_않는다() {
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 52);
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 11L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusHours(1));
        post.deactivate();

        given(entityStateStore.read("post", "11"))
            .willReturn(Optional.of(new EntityState("post", "11", txStartedAt.toLocalDate(), "DEACTIVATED", true)));

        service.record(post, txStartedAt, false);

        then(metricStore).should(never()).saveAll(anyList(), eq(txStartedAt));
        then(cohortCurrentSnapshotService).should(never()).apply(anyList());
    }

    @Test
    void 변환_결과가_비어있으면_저장하지_않는다() {
        LocalDateTime txStartedAt = LocalDateTime.now();

        service.record(List.of(), txStartedAt, false);

        then(metricStore).should(never()).saveAll(anyList(), eq(txStartedAt));
        then(cohortCurrentSnapshotService).should(never()).apply(anyList());
    }
}
