package com.bob.statistics.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("Redis 통계 일일 저장소 어댑터 테스트")
class RedisStatisticsDailyStoreAdapterTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RedisStatisticsDailyStoreAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RedisStatisticsDailyStoreAdapter(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 일일_지표_매트릭_맵_변환() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        String key = "stats:daily:trade:20260224";
        redisTemplate.opsForHash().putAll(key, Map.of(
            "metric:new_trades", "5",
            "metric:status:RESERVED", "2"
        ));

        Map<String, Long> metrics = adapter.readDailyMetrics("trade", date);

        assertThat(metrics.get("new_trades")).isEqualTo(5L);
        assertThat(metrics.get("status:RESERVED")).isEqualTo(2L);
    }

    @Test
    void 일일_지표가_없으면_빈맵_반환() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        Map<String, Long> metrics = adapter.readDailyMetrics("post", date);

        assertThat(metrics).isEmpty();
    }

    @Test
    void 일일_지표_키_삭제() {
        LocalDate date = LocalDate.of(2026, 2, 24);
        String key = "stats:daily:member:20260224";
        redisTemplate.opsForHash().put(key, "metric:new_members", "1");

        adapter.deleteDailyMetrics("member", date);

        assertThat(redisTemplate.hasKey(key)).isFalse();
    }

    @Test
    void 백업_락_획득_및_소유자_조회_후_해제() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        boolean acquired = adapter.acquire(date, "owner-1");
        String owner = adapter.currentOwner(date);
        adapter.release(date);

        assertThat(acquired).isTrue();
        assertThat(owner).isEqualTo("owner-1");
        assertThat(redisTemplate.hasKey("stats:backup:lock:20260224")).isFalse();
    }

    @Test
    void 이미_락이_있으면_획득_실패() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        boolean first = adapter.acquire(date, "owner-1");
        boolean second = adapter.acquire(date, "owner-2");

        assertThat(first).isTrue();
        assertThat(second).isFalse();
    }
}
