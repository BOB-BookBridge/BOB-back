package com.bob.statistics.application;

import static com.bob.support.fixture.member.domain.MemberFixture.createMember;
import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.member.domain.Member;
import com.bob.core.member.domain.Status;
import com.bob.core.post.domain.Post;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;
import com.bob.statistics.application.port.out.StatisticsEntityStateStore;
import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 스냅샷 변환 공통 테스트")
class StatisticsSnapshotRecordServiceTest {

    @InjectMocks
    private StatisticsSnapshotRecordService recordService;

    @Mock
    private StatisticsMetricStore metricStore;

    @Mock
    private StatisticsEntityStateStore entityStateStore;

    @Mock
    private StatisticsCohortCurrentSnapshotService cohortCurrentSnapshotService;

    @Test
    void 단일_엔티티를_통계_이벤트로_변환한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade created = createTrade(com.bob.core.trade.domain.status.Status.REQUESTED);
        ReflectionTestUtils.setField(created, "createdAt", txStartedAt.plusSeconds(1));

        recordService.record(created, txStartedAt, false);
    }

    @Test
    void 컬렉션_대상을_변환한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(com.bob.core.trade.domain.status.Status.REJECTED);
        ReflectionTestUtils.setField(trade, "id", 30L);
        ReflectionTestUtils.setField(trade, "createdAt", txStartedAt.minusDays(1));

        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 40L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusDays(1));
        post.deactivate();

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(List.of(trade, post), txStartedAt,
            false);

        assertThat(countMetric(events, "status:REJECTED")).isEqualTo(1);
        assertThat(countMetric(events, "deleted_posts")).isEqualTo(1);
    }

    @Test
    void 빈_컬렉션은_빈_목록을_반환한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(List.of(), txStartedAt, false);

        assertThat(events).isEmpty();
    }

    @Test
    void ChangeTradeStatusResult를_거래로_변환해_집계한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade rejected = createTrade(com.bob.core.trade.domain.status.Status.REJECTED);
        ReflectionTestUtils.setField(rejected, "id", 1L);
        ReflectionTestUtils.setField(rejected, "createdAt", txStartedAt.minusDays(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(
            new ChangeTradeStatusResult(rejected, null),
            txStartedAt,
            false
        );

        assertThat(countMetric(events, "new_trades")).isZero();
        assertThat(countMetric(events, "status:REJECTED")).isEqualTo(1);
    }

    @Test
    void 단일_게시글_활성화_집계_미포함() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 51L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusDays(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(post, txStartedAt, false);

        assertThat(countMetric(events, "status:ACTIVE")).isZero();
    }

    @Test
    void 컬렉션_게시글_활성화_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Post post = createPost();
        ReflectionTestUtils.setField(post, "id", 52L);
        ReflectionTestUtils.setField(post, "createdAt", txStartedAt.minusDays(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(List.of(post), txStartedAt, false);

        assertThat(countMetric(events, "status:ACTIVE")).isEqualTo(1);
    }

    @Test
    void 변환대상이_null이면_예외가_발생한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();

        assertThatThrownBy(() -> recordService.convertToMetricEvents(null, txStartedAt, false))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("must not be null");
    }

    @Test
    void 미지원_타입이면_예외가_발생한다() {
        LocalDateTime txStartedAt = LocalDateTime.now();

        assertThatThrownBy(() -> recordService.convertToMetricEvents("unsupported", txStartedAt, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported statistics target type");
    }

    @Test
    void 과거_회원_활성화_시_상태_집계_포함() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Member member = createMember();
        ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(member, "createdAt", txStartedAt.minusDays(1));
        ReflectionTestUtils.setField(member, "status", Status.ACTIVE);

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(member, txStartedAt, false);

        assertThat(countMetric(events, "status:ACTIVE")).isEqualTo(1);
        assertThat(countMetric(events, "new_members")).isZero();
    }

    private static long countMetric(List<StatisticsMetricEvent> events, String metricName) {
        return events.stream().filter(event -> metricName.equals(event.metric())).count();
    }
}
