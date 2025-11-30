package com.bob.global.ratelimit.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.global.ratelimit.repository.RateLimitRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("분산 처리 제한기 저장소 테스트")
@ContainerTest
class DistributedRateLimitRepositoryTest {

    RateLimitRepository repository;

    @Autowired
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        repository = new DistributedRateLimitRepository(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 첫_요청_허용() {
        String key = "ip:192.168.1.100";
        int maxRequest = 5;
        long windowSecond = 60;

        boolean allowed = repository.isAllowed(key, windowSecond, maxRequest);

        assertThat(allowed).isTrue();
    }

    @Test
    void 제한_내_요청_모두_허용() {
        String key = "ip:192.168.1.100";
        int maxRequest = 3;
        long windowSecond = 60;

        boolean first = repository.isAllowed(key, windowSecond, maxRequest);
        boolean second = repository.isAllowed(key, windowSecond, maxRequest);
        boolean third = repository.isAllowed(key, windowSecond, maxRequest);

        assertThat(first).isTrue();
        assertThat(second).isTrue();
        assertThat(third).isTrue();
    }

    @Test
    void 서로_다른_키_독립적_허용() {
        String key1 = "ip:192.168.1.100";
        String key2 = "ip:192.168.1.101";
        int maxRequest = 2;
        long windowSecond = 60;

        repository.isAllowed(key1, windowSecond, maxRequest);
        repository.isAllowed(key1, windowSecond, maxRequest);
        boolean key1Third = repository.isAllowed(key1, windowSecond, maxRequest);
        boolean key2First = repository.isAllowed(key2, windowSecond, maxRequest);

        assertThat(key1Third).isFalse();
        assertThat(key2First).isTrue();
    }

    @Test
    void 회원_ID_기반_요청_허용() {
        String memberKey = "member:user-uuid-123";
        int maxRequest = 10;
        long windowSecond = 60;

        boolean allowed1 = repository.isAllowed(memberKey, windowSecond, maxRequest);
        boolean allowed2 = repository.isAllowed(memberKey, windowSecond, maxRequest);

        assertThat(allowed1).isTrue();
        assertThat(allowed2).isTrue();
    }

    @Test
    void TTL_만료_후_재요청_허용() throws InterruptedException {
        String key = "ip:192.168.1.100";
        int maxRequest = 10;
        long windowSecond = 2;

        repository.isAllowed(key, windowSecond, maxRequest);

        Thread.sleep(3000);
        boolean allowedAfterTTL = repository.isAllowed(key, windowSecond, maxRequest);

        assertThat(allowedAfterTTL).isTrue();
    }

    @Test
    void 동시_요청_원자성_보장() throws InterruptedException {
        String key = "ip:192.168.1.100";
        int maxRequest = 10;
        long windowSecond = 60;

        Thread[] threads = new Thread[15];
        boolean[] results = new boolean[15];

        for (int i = 0; i < 15; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = repository.isAllowed(key, windowSecond, maxRequest);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long allowedCount = 0;
        for (boolean result : results) {
            if (result)
                allowedCount++;
        }

        assertThat(allowedCount).isEqualTo(maxRequest);
    }

    @Test
    void 최대_요청_1회_경계_테스트() {
        String key = "ip:192.168.1.100";
        int maxRequest = 1;
        long windowSecond = 60;

        boolean first = repository.isAllowed(key, windowSecond, maxRequest);
        boolean second = repository.isAllowed(key, windowSecond, maxRequest);

        assertThat(first).isTrue();
        assertThat(second).isFalse();
    }

    @Test
    void 제한_초과_요청_거부() {
        String key = "ip:192.168.1.100";
        int maxRequest = 2;
        long windowSecond = 60;

        repository.isAllowed(key, windowSecond, maxRequest);
        repository.isAllowed(key, windowSecond, maxRequest);
        boolean third = repository.isAllowed(key, windowSecond, maxRequest);

        assertThat(third).isFalse();
    }

    @Test
    void 대기_시간_계산() {
        String key = "ip:192.168.1.100";
        int maxRequest = 1;
        long windowSecond = 10;

        repository.isAllowed(key, windowSecond, maxRequest);

        long waitSeconds = repository.getWaitForRefill(key, windowSecond, maxRequest);

        assertThat(waitSeconds)
            .isGreaterThan(0)
            .isLessThanOrEqualTo(windowSecond);
    }

    @Test
    void 존재하지_않는_키_대기_시간_0() {
        String key = "ip:192.168.1.100";
        int maxRequest = 5;
        long windowSecond = 60;

        long waitSeconds = repository.getWaitForRefill(key, windowSecond, maxRequest);

        assertThat(waitSeconds).isZero();
    }

    @Test
    void Redis_데이터_저장_확인() {
        String key = "ip:192.168.1.100";
        int maxRequest = 5;
        long windowSecond = 60;

        repository.isAllowed(key, windowSecond, maxRequest);

        Boolean hasKey = redisTemplate.hasKey("rate_limit:bucket:" + key);
        assertThat(hasKey).isTrue();
    }
}
