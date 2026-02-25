package com.bob.statistics.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.statistics.domain.StatisticsMetricEvent;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("Redis 통계 저장소 테스트")
class RedisStatisticsMetricStoreTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RedisStatisticsMetricStore store;

    @BeforeEach
    void setUp() {
        store = new RedisStatisticsMetricStore(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 일일_및_시간버킷_키에_매트릭과_식별값_집계() {
        LocalDate eventDate = LocalDate.of(2026, 2, 24);
        LocalDate cohortDate = LocalDate.of(2026, 2, 20);
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 37, 11);

        var event = new StatisticsMetricEvent(eventDate, "trade", "status:REJECTED", 2, "99", cohortDate);

        store.saveAll(List.of(event), txStartedAt);

        String dailyKey = "stats:daily:trade:20260224";
        String timeBucketKey = "stats:time:trade:20260224:1400";
        String metricField = "metric:status:REJECTED";

        assertThat(redisTemplate.opsForHash().get(dailyKey, metricField)).isEqualTo("2");
        assertThat(redisTemplate.opsForHash().get(timeBucketKey, metricField)).isEqualTo("2");
    }

    @Test
    void 트랜잭션_시간은_10분_단위로_내림되어_시간버킷_키_생성() {
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 59, 59);
        var event = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "post", "deleted_posts", 1, "1",
            LocalDate.of(2026, 2, 24));

        store.saveAll(List.of(event), txStartedAt);

        String timeBucketKey = "stats:time:post:20260224:1400";
        assertThat(redisTemplate.hasKey(timeBucketKey)).isTrue();
        assertThat(redisTemplate.opsForHash().get(timeBucketKey, "metric:deleted_posts")).isEqualTo("1");
    }

    @Test
    void 일일데이터의_TTL_30일_시간버킷의_TTL_3일() {
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 37, 11);
        var event = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "trade", "new_trades", 1, "10",
            LocalDate.of(2026, 2, 24));

        store.saveAll(List.of(event), txStartedAt);

        String dailyKey = "stats:daily:trade:20260224";
        String timeBucketKey = "stats:time:trade:20260224:1400";

        Long dailyTtl = redisTemplate.getExpire(dailyKey);
        Long timeBucketTtl = redisTemplate.getExpire(timeBucketKey);

        assertThat(dailyTtl).isNotNull().isGreaterThan(0L).isLessThanOrEqualTo(30 * 24 * 60 * 60);
        assertThat(timeBucketTtl).isNotNull().isGreaterThan(0L).isLessThanOrEqualTo(3 * 24 * 60 * 60);
        assertThat(dailyTtl).isGreaterThan(timeBucketTtl);
    }

    @Test
    void 빈_이벤트_or_0값_or_식별값_불충분_조건은_수집되지_않는다() {
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 9, 1, 0);
        var zeroValue = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "member", "new_members", 0, "100",
            LocalDate.of(2026, 2, 24));

        var noRaw = new StatisticsMetricEvent(LocalDate.of(2026, 2, 24), "member", "status:ACTIVE", 1, null, null);

        store.saveAll(null, txStartedAt);
        store.saveAll(List.of(), txStartedAt);
        store.saveAll(List.of(zeroValue, noRaw), txStartedAt);

        String dailyKey = "stats:daily:member:20260224";
        assertThat(redisTemplate.opsForHash().get(dailyKey, "metric:new_members")).isNull();
        assertThat(redisTemplate.opsForHash().get(dailyKey, "metric:status:ACTIVE")).isEqualTo("1");

        assertThat(redisTemplate.opsForHash().keys(dailyKey))
            .allMatch(key -> String.valueOf(key).startsWith("metric:"));
    }

    @Test
    void 시간_버킷_조회() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        LocalDateTime txStartedAt = LocalDateTime.of(2026, 2, 24, 14, 39, 0);

        var event = new StatisticsMetricEvent(date, "member", "new_members", 3, "1", date);
        store.saveAll(List.of(event), txStartedAt);

        var metrics = store.readTimeMetrics("member", date, LocalTime.of(14, 0));

        assertThat(metrics.get("new_members")).isEqualTo(3L);
    }
}
