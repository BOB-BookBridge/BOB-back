package com.bob.statistics.application;

import static com.bob.support.fixture.post.domain.PostFixture.createPost;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.post.domain.Post;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.status.Status;
import com.bob.statistics.domain.StatisticsMetricEvent;

@DisplayName("통계 스냅샷 변환 공통 테스트")
class StatisticsSnapshotRecordServiceTest {

    final StatisticsSnapshotRecordService recordService = new StatisticsSnapshotRecordService();

    @Test
    void 단일_이벤트_기반_기록_수집() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade created = createTrade(Status.REQUESTED);
        ReflectionTestUtils.setField(created, "createdAt", txStartedAt.plusSeconds(1));

        recordService.record(created, txStartedAt, false);
    }

    @Test
    void 컬렉션_기반_기록_수집() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(Status.REJECTED);
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
    void ChangeTradeStatusResult는_trade로_변환되어_집계된다() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade rejected = createTrade(Status.REJECTED);
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

    private static long countMetric(List<StatisticsMetricEvent> events, String metricName) {
        return events.stream().filter(event -> metricName.equals(event.metric())).count();
    }
}
