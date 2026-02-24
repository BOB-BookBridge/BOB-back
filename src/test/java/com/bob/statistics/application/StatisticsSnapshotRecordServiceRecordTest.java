package com.bob.statistics.application;

import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import com.bob.core.trade.domain.Trade;
import com.bob.core.trade.domain.status.Status;
import com.bob.statistics.application.port.out.StatisticsMetricStore;

@ExtendWith(MockitoExtension.class)
@DisplayName("통계 스냅샷 기록 서비스 저장 호출 테스트")
class StatisticsSnapshotRecordServiceRecordTest {

    @Mock
    private StatisticsMetricStore metricStore;

    @Test
    void 스냅샷_기록_시_매트릭_저장() {
        StatisticsSnapshotRecordService service = new StatisticsSnapshotRecordService(metricStore);
        LocalDateTime txStartedAt = LocalDateTime.now();
        Trade trade = createTrade(Status.REQUESTED);
        ReflectionTestUtils.setField(trade, "id", 1L);
        ReflectionTestUtils.setField(trade, "createdAt", txStartedAt.plusSeconds(1));

        service.record(trade, txStartedAt, false);

        then(metricStore).should().saveAll(anyList(), eq(txStartedAt));
    }
}
