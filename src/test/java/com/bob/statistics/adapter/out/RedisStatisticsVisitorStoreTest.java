package com.bob.statistics.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("Redis 방문자 저장소 테스트")
class RedisStatisticsVisitorStoreTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RedisStatisticsVisitorStore store;

    @BeforeEach
    void setUp() {
        store = new RedisStatisticsVisitorStore(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 동일_ip는_중복없이_하루_한번만_집계() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        store.addDailyVisitor(date, "127.0.0.1");
        store.addDailyVisitor(date, "127.0.0.1");
        store.addDailyVisitor(date, "127.0.0.2");

        assertThat(store.countDailyVisitors(date)).isEqualTo(2);
    }

    @Test
    void 방문자_키가_없으면_0_반환() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        assertThat(store.countDailyVisitors(date)).isZero();
    }

    @Test
    void 방문자_키_삭제() {
        LocalDate date = LocalDate.of(2026, 2, 24);

        store.addDailyVisitor(date, "127.0.0.3");
        store.deleteDailyVisitors(date);

        assertThat(store.countDailyVisitors(date)).isZero();
    }
}
