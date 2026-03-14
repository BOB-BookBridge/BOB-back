package com.bob.statistics.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.statistics.application.dto.result.StatisticsTradeSummary;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("거래 통계 조회 테스트")
@ContainerTest
record StatisticsTradeReaderTest(
    StatisticsTradeReader statisticsTradeReader,
    StatisticsTradeDailySnapshotRepository tradeDailySnapshotRepository,

    StringRedisTemplate redisTemplate
) {

    @BeforeEach
    void setUp() {
        tradeDailySnapshotRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 과거_거래_통계_조회() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        StatisticsTradeDailySnapshot snapshot = StatisticsTradeDailySnapshot.createEmpty(yesterday);
        snapshot.addCanceledTrades(4);
        snapshot.addRejectedTrades(1);
        snapshot.addRequestedTrades(2);
        snapshot.addAcceptedTrades(3);
        snapshot.addReservedTrades(5);
        snapshot.addCompletedTrades(6);
        tradeDailySnapshotRepository.save(snapshot);

        redisTemplate.opsForHash().putAll("stats:daily:trade:" + todayText, Map.of(
            "metric:canceled_trades", "7",
            "metric:rejected_trades", "8",
            "metric:requested_trades", "10",
            "metric:accepted_trades", "9",
            "metric:reserved_trades", "6",
            "metric:completed_trades", "5"
        ));

        StatisticsTradeSummary result = statisticsTradeReader.readTrade(yesterday, today);

        assertThat(result.canceled()).isEqualTo(11);
        assertThat(result.rejected()).isEqualTo(9);
        assertThat(result.requested()).isEqualTo(12);
        assertThat(result.accepted()).isEqualTo(12);
        assertThat(result.reserved()).isEqualTo(11);
        assertThat(result.completed()).isEqualTo(11);
    }

    @Test
    void 당일_거래_통계_조회() {
        LocalDate today = LocalDate.now();
        String todayText = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        redisTemplate.opsForHash().putAll("stats:daily:trade:" + todayText, Map.of(
            "metric:canceled_trades", "1",
            "metric:rejected_trades", "2",
            "metric:requested_trades", "3",
            "metric:accepted_trades", "4",
            "metric:reserved_trades", "5",
            "metric:completed_trades", "6"
        ));

        StatisticsTradeSummary result = statisticsTradeReader.readTrade(today, today);

        assertThat(result.canceled()).isEqualTo(1);
        assertThat(result.rejected()).isEqualTo(2);
        assertThat(result.requested()).isEqualTo(3);
        assertThat(result.accepted()).isEqualTo(4);
        assertThat(result.reserved()).isEqualTo(5);
        assertThat(result.completed()).isEqualTo(6);
    }

    @Test
    void 빈_거래_통계_조회() {
        LocalDate from = LocalDate.now().minusMonths(2);
        LocalDate to = LocalDate.now().minusMonths(1);

        StatisticsTradeSummary result = statisticsTradeReader.readTrade(from, to);

        assertThat(result.canceled()).isZero();
        assertThat(result.rejected()).isZero();
        assertThat(result.requested()).isZero();
        assertThat(result.accepted()).isZero();
        assertThat(result.reserved()).isZero();
        assertThat(result.completed()).isZero();
    }
}
