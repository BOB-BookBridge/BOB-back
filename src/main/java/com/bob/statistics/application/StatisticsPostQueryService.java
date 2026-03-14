package com.bob.statistics.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.post.domain.repository.PostRepository;
import com.bob.statistics.application.dto.result.StatisticsPostSummary;
import com.bob.statistics.application.port.in.StatisticsPostReader;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;
import com.bob.statistics.domain.StatisticsPostDailySnapshot;
import com.bob.statistics.domain.repository.StatisticsPostDailySnapshotRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsPostQueryService implements StatisticsPostReader {

    private final PostRepository postRepository;
    private final StatisticsPostDailySnapshotRepository postDailySnapshotRepository;
    private final StatisticsDailyMetricStore dailyMetricStore;

    @Override
    public StatisticsPostSummary readPost(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();

        long registered = 0;
        long deleted = 0;

        LocalDate mysqlTo = to.isEqual(today) ? to.minusDays(1) : to;
        if (!from.isAfter(mysqlTo)) {
            for (StatisticsPostDailySnapshot snapshot :
                postDailySnapshotRepository.findAllBySnapshotDateBetweenOrderBySnapshotDateAsc(from, mysqlTo)) {
                registered += snapshot.getNewPosts();
                deleted += snapshot.getDeletedPosts() + snapshot.getBannedPosts();
            }
        }

        if (to.isEqual(today)) {
            Map<String, Long> todayMetrics = dailyMetricStore.readDailyMetrics("post", today);
            registered += metric(todayMetrics, "new_posts");
            deleted += postDeleted(todayMetrics) + postBanned(todayMetrics);
        }

        List<StatisticsPostSummary.CategoryDistribution> categoryDistribution =
            postRepository.findCategoryDistribution(fromDateTime, toExclusive).stream()
                .map(p -> new StatisticsPostSummary.CategoryDistribution(p.getCategoryId(), p.getCount()))
                .toList();

        List<StatisticsPostSummary.AreaDistribution> areaDistribution =
            postRepository.findAreaDistribution(fromDateTime, toExclusive).stream()
                .map(p -> new StatisticsPostSummary.AreaDistribution(p.getEmdId(), p.getCount()))
                .toList();

        return new StatisticsPostSummary(registered, deleted, categoryDistribution, areaDistribution);
    }

    private static long metric(Map<String, Long> metrics, String key) {
        return metrics.getOrDefault(key, 0L);
    }

    private static long postDeleted(Map<String, Long> metrics) {
        return metrics.getOrDefault("deleted_posts", metrics.getOrDefault("status:DEACTIVATED", 0L));
    }

    private static long postBanned(Map<String, Long> metrics) {
        return metrics.getOrDefault("banned_posts", metrics.getOrDefault("status:BANNED", 0L));
    }
}
