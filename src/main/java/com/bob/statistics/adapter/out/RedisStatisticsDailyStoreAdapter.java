package com.bob.statistics.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bob.statistics.application.port.out.StatisticsBackupLockStore;
import com.bob.statistics.application.port.out.StatisticsDailyMetricStore;

@Repository
@RequiredArgsConstructor
public class RedisStatisticsDailyStoreAdapter implements StatisticsDailyMetricStore, StatisticsBackupLockStore {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String METRIC_PREFIX = "metric:";
    private static final Duration LOCK_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;

    @Override
    public Map<String, Long> readDailyMetrics(String domain, LocalDate snapshotDate) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(dailyKey(domain, snapshotDate));
        if (raw.isEmpty())
            return Map.of();

        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : raw.entrySet()) {
            String metric = metricName(entry.getKey());
            long value = longValue(entry.getValue());
            result.put(metric, value);
        }
        return result;
    }

    @Override
    public void deleteDailyMetrics(String domain, LocalDate snapshotDate) {
        redisTemplate.delete(dailyKey(domain, snapshotDate));
    }

    @Override
    public boolean acquire(LocalDate snapshotDate, String owner) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey(snapshotDate), owner, LOCK_TTL);
        return Boolean.TRUE.equals(acquired);
    }

    @Override
    public String currentOwner(LocalDate snapshotDate) {
        return redisTemplate.opsForValue().get(lockKey(snapshotDate));
    }

    @Override
    public void release(LocalDate snapshotDate) {
        redisTemplate.delete(lockKey(snapshotDate));
    }

    private static String dailyKey(String domain, LocalDate date) {
        return "stats:daily:" + domain + ":" + DAY.format(date);
    }

    private static String lockKey(LocalDate date) {
        return "stats:backup:lock:" + DAY.format(date);
    }

    private static String metricName(Object key) {
        String raw = String.valueOf(key);
        if (raw.startsWith(METRIC_PREFIX))
            return raw.substring(METRIC_PREFIX.length());

        return raw;
    }

    private static long longValue(Object value) {
        if (value instanceof Number number)
            return number.longValue();

        return Long.parseLong(String.valueOf(value));
    }
}
