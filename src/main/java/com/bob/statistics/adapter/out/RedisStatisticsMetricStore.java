package com.bob.statistics.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.application.port.out.StatisticsTimeMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@Repository
@RequiredArgsConstructor
public class RedisStatisticsMetricStore implements StatisticsMetricStore, StatisticsTimeMetricStore {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HHmm");

    private static final Duration DAILY_TTL = Duration.ofDays(30);
    private static final Duration REALTIME_TTL = Duration.ofDays(3);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveAll(List<StatisticsMetricEvent> events, LocalDateTime txStartedAt) {
        if (events == null || events.isEmpty())
            return;

        for (StatisticsMetricEvent event : events) {
            long value = event.value();
            if (value == 0)
                continue;

            String dailyKey = dailyKey(event.domain(), event.eventDate());
            String realtimeKey = realtimeKey(event.domain(), txStartedAt);
            String metricField = metricField(event.metric());

            redisTemplate.opsForHash().increment(dailyKey, metricField, value);
            redisTemplate.opsForHash().increment(realtimeKey, metricField, value);
            redisTemplate.expire(dailyKey, DAILY_TTL);
            redisTemplate.expire(realtimeKey, REALTIME_TTL);
        }
    }

    @Override
    public Map<String, Long> readTimeMetrics(String domain, LocalDate date, LocalTime timeBucket) {
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(timeKey(domain, date, timeBucket));
        if (raw.isEmpty())
            return Map.of();

        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : raw.entrySet())
            result.put(metricName(entry.getKey()), longValue(entry.getValue()));

        return result;
    }

    private static String dailyKey(String domain, LocalDate date) {
        return "stats:daily:" + domain + ":" + DAY.format(date);
    }

    private static String realtimeKey(String domain, LocalDateTime txStartedAt) {
        LocalDate date = txStartedAt.toLocalDate();
        LocalTime bucket = toHourBucket(txStartedAt.toLocalTime());
        return timeKey(domain, date, bucket);
    }

    private static String timeKey(String domain, LocalDate date, LocalTime bucket) {
        return "stats:time:" + domain + ":" + DAY.format(date) + ":" + HM.format(bucket);
    }

    private static LocalTime toHourBucket(LocalTime time) {
        return LocalTime.of(time.getHour(), 0);
    }

    private static String metricField(String metric) {
        return "metric:" + metric;
    }

    private static String metricName(Object key) {
        String raw = String.valueOf(key);
        if (raw.startsWith("metric:"))
            return raw.substring("metric:".length());

        return raw;
    }

    private static long longValue(Object value) {
        if (value instanceof Number number)
            return number.longValue();

        return Long.parseLong(String.valueOf(value));
    }
}
