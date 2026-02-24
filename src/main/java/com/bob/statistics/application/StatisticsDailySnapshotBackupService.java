package com.bob.statistics.application;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.statistics.application.port.out.StatisticsBackupLockStore;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;
import com.bob.statistics.application.port.out.StatisticsVisitorStore;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.StatisticsTradeDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;
import com.bob.statistics.domain.repository.StatisticsTradeDailySnapshotRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsDailySnapshotBackupService {

    private final StatisticsDailyMetricStore dailyMetricStore;
    private final StatisticsBackupLockStore backupLockStore;
    private final StatisticsVisitorStore visitorStore;
    private final StatisticsMemberDailySnapshotRepository memberRepository;
    private final StatisticsPostDailySnapshotRepository postRepository;
    private final StatisticsTradeDailySnapshotRepository tradeRepository;

    @Transactional
    public void backup(LocalDate snapshotDate) {
        String owner = UUID.randomUUID().toString();

        if (!backupLockStore.acquire(snapshotDate, owner)) {
            log.info("statistics backup skipped by lock. date={}", snapshotDate);
            return;
        }

        try {
            backupMember(snapshotDate);
            backupPost(snapshotDate);
            backupTrade(snapshotDate);
            log.info("statistics backup completed. date={}", snapshotDate);
        } catch (Exception e) {
            log.error("statistics backup failed. date={}", snapshotDate, e);
            throw e;
        } finally {
            releaseLock(snapshotDate, owner);
        }
    }

    private void backupMember(LocalDate snapshotDate) {
        Map<String, Long> metrics = dailyMetricStore.readDailyMetrics("member", snapshotDate);
        long dailyVisitors = visitorStore.countDailyVisitors(snapshotDate);

        if (metrics.isEmpty() && dailyVisitors == 0)
            return;

        StatisticsMemberDailySnapshot snapshot = memberRepository.findBySnapshotDate(snapshotDate)
            .orElse(StatisticsMemberDailySnapshot.createEmpty(snapshotDate));

        for (Map.Entry<String, Long> entry : metrics.entrySet()) {
            String metric = entry.getKey();
            long value = entry.getValue();

            switch (metric) {
                case "new_members" -> snapshot.addNewMembers(value);
                case "deactivated_members", "status:DEACTIVATED" -> snapshot.addDeactivatedMembers(value);
                case "banned_members", "status:BANNED" -> snapshot.addBannedMembers(value);
                default -> {
                }
            }
        }

        snapshot.addDailyVisitors(dailyVisitors);
        memberRepository.save(snapshot);
        dailyMetricStore.deleteDailyMetrics("member", snapshotDate);
        visitorStore.deleteDailyVisitors(snapshotDate);
    }

    private void backupPost(LocalDate snapshotDate) {
        Map<String, Long> metrics = dailyMetricStore.readDailyMetrics("post", snapshotDate);
        if (metrics.isEmpty())
            return;

        StatisticsPostDailySnapshot snapshot = postRepository.findBySnapshotDate(snapshotDate)
            .orElse(StatisticsPostDailySnapshot.createEmpty(snapshotDate));

        for (Map.Entry<String, Long> entry : metrics.entrySet()) {
            String metric = entry.getKey();
            long value = entry.getValue();

            switch (metric) {
                case "new_posts" -> snapshot.addNewPosts(value);
                case "deleted_posts", "status:DEACTIVATED" -> snapshot.addDeletedPosts(value);
                case "banned_posts", "status:BANNED" -> snapshot.addBannedPosts(value);
                default -> {
                }
            }
        }

        postRepository.save(snapshot);
        dailyMetricStore.deleteDailyMetrics("post", snapshotDate);
    }

    private void backupTrade(LocalDate snapshotDate) {
        Map<String, Long> metrics = dailyMetricStore.readDailyMetrics("trade", snapshotDate);
        if (metrics.isEmpty())
            return;

        StatisticsTradeDailySnapshot snapshot = tradeRepository.findBySnapshotDate(snapshotDate)
            .orElse(StatisticsTradeDailySnapshot.createEmpty(snapshotDate));

        for (Map.Entry<String, Long> entry : metrics.entrySet()) {
            String metric = entry.getKey();
            long value = entry.getValue();

            switch (metric) {
                case "new_trades" -> snapshot.addNewTrades(value);
                case "requested_trades", "status:REQUESTED" -> snapshot.addRequestedTrades(value);
                case "accepted_trades", "status:ACCEPTED" -> snapshot.addAcceptedTrades(value);
                case "rejected_trades", "status:REJECTED" -> snapshot.addRejectedTrades(value);
                case "canceled_trades", "status:CANCELED" -> snapshot.addCanceledTrades(value);
                case "reserved_trades", "status:RESERVED" -> snapshot.addReservedTrades(value);
                case "completed_trades", "status:COMPLETED" -> snapshot.addCompletedTrades(value);
                default -> {
                }
            }
        }

        tradeRepository.save(snapshot);
        dailyMetricStore.deleteDailyMetrics("trade", snapshotDate);
    }

    private void releaseLock(LocalDate snapshotDate, String owner) {
        String current = backupLockStore.currentOwner(snapshotDate);
        if (owner.equals(current))
            backupLockStore.release(snapshotDate);
    }
}
