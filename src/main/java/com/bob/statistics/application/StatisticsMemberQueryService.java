package com.bob.statistics.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.statistics.application.dto.result.StatisticsMemberTimeMatric;
import com.bob.statistics.application.dto.result.StatisticsMemberTimeSeries;
import com.bob.statistics.application.port.in.StatisticsMemberReader;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;
import com.bob.statistics.application.port.out.StatisticsTimeMetricStore;
import com.bob.statistics.application.port.out.StatisticsVisitorStore;
import com.bob.statistics.domain.StatisticsMemberDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsMemberDailySnapshotRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsMemberQueryService implements StatisticsMemberReader {

    private final StatisticsMemberDailySnapshotRepository memberDailySnapshotRepository;

    private final StatisticsDailyMetricStore dailyMetricStore;
    private final StatisticsTimeMetricStore timeMetricStore;
    private final StatisticsVisitorStore visitorStore;

    @Override
    public StatisticsMemberTimeSeries readMember(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();

        if (from.isEqual(today) && to.isEqual(today))
            return readTodayTimeSeries(today);

        return readDailySeries(from, to, today);
    }

    private StatisticsMemberTimeSeries readTodayTimeSeries(LocalDate today) {
        Map<String, Long> todayDaily = dailyMetricStore.readDailyMetrics("member", today);

        long visitors = visitorStore.countDailyVisitors(today);
        long newMembers = metric(todayDaily);
        long deactivatedMembers = memberDeactivated(todayDaily);
        long bannedMembers = memberBanned(todayDaily);

        List<StatisticsMemberTimeMatric> points = buildTodayPoints(today);

        return new StatisticsMemberTimeSeries(visitors, newMembers, deactivatedMembers, bannedMembers, points);
    }

    private List<StatisticsMemberTimeMatric> buildTodayPoints(LocalDate today) {
        List<StatisticsMemberTimeMatric> points = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            LocalTime bucket = LocalTime.MIN.plusHours(i);
            Map<String, Long> metrics = timeMetricStore.readTimeMetrics("member", today, bucket);

            points.add(new StatisticsMemberTimeMatric(
                LocalDateTime.of(today, bucket),
                visitorStore.countTimeVisitors(today, bucket),
                metric(metrics),
                memberDeactivated(metrics),
                memberBanned(metrics)
            ));
        }

        return points;
    }

    private StatisticsMemberTimeSeries readDailySeries(LocalDate from, LocalDate to, LocalDate today) {
        LocalDate mysqlTo = to.isEqual(today) ? to.minusDays(1) : to;
        Map<LocalDate, StatisticsMemberDailySnapshot> snapshotsByDate = new HashMap<>();

        memberDailySnapshotRepository.findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(from, mysqlTo)
            .forEach(snapshot -> snapshotsByDate.put(snapshot.getSnapshotDate(), snapshot));

        long visitors = 0;
        long newMembers = 0;
        long deactivatedMembers = 0;
        long bannedMembers = 0;

        List<StatisticsMemberTimeMatric> points = new ArrayList<>();
        for (LocalDate cursor = from; !cursor.isAfter(to); cursor = cursor.plusDays(1)) {
            long dailyVisitors;
            long dailyNewMembers;
            long dailyDeactivatedMembers;
            long dailyBannedMembers;

            if (cursor.isEqual(today)) {
                Map<String, Long> todayMetrics = dailyMetricStore.readDailyMetrics("member", today);
                dailyVisitors = visitorStore.countDailyVisitors(today);
                dailyNewMembers = metric(todayMetrics);
                dailyDeactivatedMembers = memberDeactivated(todayMetrics);
                dailyBannedMembers = memberBanned(todayMetrics);
            } else {
                StatisticsMemberDailySnapshot snapshot = snapshotsByDate.get(cursor);
                if (snapshot == null) {
                    dailyVisitors = 0;
                    dailyNewMembers = 0;
                    dailyDeactivatedMembers = 0;
                    dailyBannedMembers = 0;
                } else {
                    dailyVisitors = snapshot.getDailyVisitors();
                    dailyNewMembers = snapshot.getNewMembers();
                    dailyDeactivatedMembers = snapshot.getDeactivatedMembers();
                    dailyBannedMembers = snapshot.getBannedMembers();
                }
            }

            visitors += dailyVisitors;
            newMembers += dailyNewMembers;
            deactivatedMembers += dailyDeactivatedMembers;
            bannedMembers += dailyBannedMembers;

            points.add(new StatisticsMemberTimeMatric(
                cursor.atStartOfDay(),
                dailyVisitors,
                dailyNewMembers,
                dailyDeactivatedMembers,
                dailyBannedMembers
            ));
        }

        return new StatisticsMemberTimeSeries(visitors, newMembers, deactivatedMembers, bannedMembers, points);
    }

    private static long memberDeactivated(Map<String, Long> metrics) {
        return metrics.getOrDefault("deactivated_members", metrics.getOrDefault("status:DEACTIVATED", 0L));
    }

    private static long memberBanned(Map<String, Long> metrics) {
        return metrics.getOrDefault("banned_members", metrics.getOrDefault("status:BANNED", 0L));
    }

    private static long metric(Map<String, Long> metrics) {
        return metrics.getOrDefault("new_members", 0L);
    }
}
