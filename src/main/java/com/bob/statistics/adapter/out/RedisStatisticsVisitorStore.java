package com.bob.statistics.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bob.statistics.application.port.out.StatisticsVisitorStore;

@Repository
@RequiredArgsConstructor
public class RedisStatisticsVisitorStore implements StatisticsVisitorStore {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Duration VISITOR_TTL = Duration.ofDays(40);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addDailyVisitor(LocalDate date, String ip) {
        String key = dailyVisitorKey(date);
        redisTemplate.opsForSet().add(key, ip);
        redisTemplate.expire(key, VISITOR_TTL);
    }

    @Override
    public long countDailyVisitors(LocalDate date) {
        return Optional.ofNullable(redisTemplate.opsForSet().size(dailyVisitorKey(date))).orElse(0L);
    }

    @Override
    public void deleteDailyVisitors(LocalDate date) {
        redisTemplate.delete(dailyVisitorKey(date));
    }

    private static String dailyVisitorKey(LocalDate date) {
        return "stats:visitors:" + DAY.format(date);
    }
}
