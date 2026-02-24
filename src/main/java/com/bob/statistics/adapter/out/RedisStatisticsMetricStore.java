package com.bob.statistics.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bob.statistics.application.port.out.StatisticsMetricStore;
import com.bob.statistics.domain.StatisticsMetricEvent;

@Repository
@RequiredArgsConstructor
public class RedisStatisticsMetricStore implements StatisticsMetricStore {

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

    private static String dailyKey(String domain, LocalDate date) {
        return "stats:daily:" + domain + ":" + DAY.format(date);
    }

    private static String realtimeKey(String domain, LocalDateTime txStartedAt) {
        LocalDate date = txStartedAt.toLocalDate();
        LocalTime bucket = toTenMinuteBucket(txStartedAt.toLocalTime());
        return "stats:time:" + domain + ":" + DAY.format(date) + ":" + HM.format(bucket);
    }

    private static LocalTime toTenMinuteBucket(LocalTime time) {
        int minute = (time.getMinute() / 10) * 10;
        return LocalTime.of(time.getHour(), minute);
    }

    private static String metricField(String metric) {
        return "metric:" + metric;
    }
}
