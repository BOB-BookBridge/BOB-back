package com.bob.statistics.application;

import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.status.Status;
import com.bob.statistics.domain.StatisticsMetricEvent;

@DisplayName("거래 통계 스냅샷 변환 테스트")
class TradeStatisticsSnapshotRecordServiceTest {

    final StatisticsSnapshotRecordService recordService = new StatisticsSnapshotRecordService();

    @Test
    void 거래_생성_집계() {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade created = createTrade(Status.REQUESTED);
        ReflectionTestUtils.setField(created, "id", 1L);
        ReflectionTestUtils.setField(created, "createdAt", txStartedAt.plusSeconds(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(created, txStartedAt, false);

        assertThat(countMetric(events, "new_trades")).isEqualTo(1);
        assertThat(countMetric(events, "status:REQUESTED")).isEqualTo(1);
    }

    @Test
    void 거래_물품_변경에_따른_REQUESTED_상태_전이_시_집계에_포함되지않음() {
        // 사전조건: 거래 상태가 [취소, 거절] 상태에서 교환 요청자가 거래 물품을 재등록하는경우 REQUESTED로 상태 전이
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade requestedAgain = createTrade(Status.REQUESTED);
        ReflectionTestUtils.setField(requestedAgain, "id", 99L);
        ReflectionTestUtils.setField(requestedAgain, "createdAt", txStartedAt.minusDays(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(requestedAgain, txStartedAt, false);

        assertThat(countMetric(events, "new_trades")).isZero();
        assertThat(countMetric(events, "status:REQUESTED")).isZero();
    }

    @Test
    void 거래_수락_상태_변경_집계() {
        assertTradeStatusMetric(Status.ACCEPTED, "status:ACCEPTED");
    }

    @Test
    void 거래_예약_상태_변경_집계() {
        assertTradeStatusMetric(Status.RESERVED, "status:RESERVED");
    }

    @Test
    void 거래_완료_상태_변경_집계() {
        assertTradeStatusMetric(Status.COMPLETED, "status:COMPLETED");
    }

    @Test
    void 거래_취소_상태_변경_집계() {
        assertTradeStatusMetric(Status.CANCELED, "status:CANCELED");
    }

    @Test
    void 거래_거절_상태_변경_집계() {
        assertTradeStatusMetric(Status.REJECTED, "status:REJECTED");
    }

    private void assertTradeStatusMetric(Status status, String metricName) {
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(status);
        ReflectionTestUtils.setField(trade, "id", 7L);
        ReflectionTestUtils.setField(trade, "createdAt", txStartedAt.minusDays(1));

        List<StatisticsMetricEvent> events = recordService.convertToMetricEvents(trade, txStartedAt, false);

        assertThat(countMetric(events, "new_trades")).isZero();
        assertThat(countMetric(events, metricName)).isEqualTo(1);
    }

    private static long countMetric(List<StatisticsMetricEvent> events, String metricName) {
        return events.stream().filter(event -> metricName.equals(event.metric())).count();
    }
}
