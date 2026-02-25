package com.bob.statistics.application;

import java.time.LocalDate;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.statistics.application.dto.result.StatisticsTradeSummary;
import com.bob.statistics.application.port.in.StatisticsTradeReader;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsTradeQueryService implements StatisticsTradeReader {

    private final StatisticsTradeDailySnapshotRepository tradeDailySnapshotRepository;

    private final StatisticsDailyMetricStore dailyMetricStore;

    @Override
    public StatisticsTradeSummary readTrade(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();

        long canceled = 0;
        long rejected = 0;
        long requested = 0;
        long accepted = 0;
        long reserved = 0;
        long completed = 0;

        LocalDate mysqlTo = to.isEqual(today) ? to.minusDays(1) : to;
        if (!from.isAfter(mysqlTo)) {
            for (StatisticsTradeDailySnapshot snapshot :
                tradeDailySnapshotRepository.findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(from, mysqlTo)) {
                canceled += snapshot.getCanceledTrades();
                rejected += snapshot.getRejectedTrades();
                requested += snapshot.getRequestedTrades();
                accepted += snapshot.getAcceptedTrades();
                reserved += snapshot.getReservedTrades();
                completed += snapshot.getCompletedTrades();
            }
        }

        if (to.isEqual(today)) {
            Map<String, Long> todayMetrics = dailyMetricStore.readDailyMetrics("trade", today);
            canceled += tradeCanceled(todayMetrics);
            rejected += tradeRejected(todayMetrics);
            requested += tradeRequested(todayMetrics);
            accepted += tradeAccepted(todayMetrics);
            reserved += tradeReserved(todayMetrics);
            completed += tradeCompleted(todayMetrics);
        }

        return new StatisticsTradeSummary(canceled, rejected, requested, accepted, reserved, completed);
    }

    private static long tradeCanceled(Map<String, Long> metrics) {
        return metrics.getOrDefault("canceled_trades", metrics.getOrDefault("status:CANCELED", 0L));
    }

    private static long tradeRejected(Map<String, Long> metrics) {
        return metrics.getOrDefault("rejected_trades", metrics.getOrDefault("status:REJECTED", 0L));
    }

    private static long tradeRequested(Map<String, Long> metrics) {
        return metrics.getOrDefault("requested_trades", metrics.getOrDefault("status:REQUESTED", 0L));
    }

    private static long tradeAccepted(Map<String, Long> metrics) {
        return metrics.getOrDefault("accepted_trades", metrics.getOrDefault("status:ACCEPTED", 0L));
    }

    private static long tradeReserved(Map<String, Long> metrics) {
        return metrics.getOrDefault("reserved_trades", metrics.getOrDefault("status:RESERVED", 0L));
    }

    private static long tradeCompleted(Map<String, Long> metrics) {
        return metrics.getOrDefault("completed_trades", metrics.getOrDefault("status:COMPLETED", 0L));
    }
}
