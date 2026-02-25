package com.bob.statistics.adapter.out;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HHmm");
    private static final Duration VISITOR_TTL = Duration.ofDays(40);
    private static final Duration VISITOR_TIME_TTL = Duration.ofDays(3);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addDailyVisitor(LocalDate date, String ip) {
        String key = dailyVisitorKey(date);
        redisTemplate.opsForSet().add(key, ip);
        redisTemplate.expire(key, VISITOR_TTL);

        LocalTime bucket = toHourBucket(LocalDateTime.now(KST).toLocalTime());
        String timeKey = timeVisitorKey(date, bucket);
        redisTemplate.opsForSet().add(timeKey, ip);
        redisTemplate.expire(timeKey, VISITOR_TIME_TTL);
    }

    @Override
    public long countDailyVisitors(LocalDate date) {
        return Optional.ofNullable(redisTemplate.opsForSet().size(dailyVisitorKey(date))).orElse(0L);
    }

    @Override
    public long countTimeVisitors(LocalDate date, LocalTime timeBucket) {
        return Optional.ofNullable(redisTemplate.opsForSet().size(timeVisitorKey(date, timeBucket))).orElse(0L);
    }

    @Override
    public void deleteDailyVisitors(LocalDate date) {
        redisTemplate.delete(dailyVisitorKey(date));
    }

    private static String dailyVisitorKey(LocalDate date) {
        return "stats:visitors:" + DAY.format(date);
    }

    private static String timeVisitorKey(LocalDate date, LocalTime timeBucket) {
        return "stats:time:visitor:" + DAY.format(date) + ":" + HM.format(timeBucket);
    }

    private static LocalTime toHourBucket(LocalTime time) {
        return LocalTime.of(time.getHour(), 0);
    }
}
