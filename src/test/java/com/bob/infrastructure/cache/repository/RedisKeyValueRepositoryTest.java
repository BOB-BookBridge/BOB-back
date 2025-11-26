package com.bob.infrastructure.cache.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.infrastructure.cache.repository.impl.RedisKeyValueRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("REDIS 저장소 테스트")
@ContainerTest
record RedisKeyValueRepositoryTest(RedisKeyValueRepository repository, StringRedisTemplate redisTemplate) {

    @AfterEach
    void tearDown() {
        repository.clear();
    }

    @Test
    void 저장_및_조회() throws InterruptedException {
        String key = "test:key";
        repository.set(key, "test-value", Duration.ofMinutes(3));

        Optional<String> result = repository.get(key);

        assertThat(result).isEqualTo(Optional.of("test-value"));

        // ttl 적용 확인
        key = "test:ttl";
        repository.set(key, "test-value", Duration.ofMillis(100)); // ttl=100ms
        Thread.sleep(200); // sleep 200ms

        result = repository.get(key);

        assertThat(result).isEmpty();

        // 존재하지 않는 key 조회
        key = "test:none";

        result = repository.get(key);

        assertThat(result).isEmpty();
    }

    @Test
    void 키_존재_확인() {
        String key = "test:key";
        repository.set(key, "value", Duration.ofMinutes(3));

        assertThat(repository.exists(key)).isTrue();
        assertThat(repository.exists("none")).isFalse();
    }

    @Test
    void 키_삭제() {
        String key = "test:key";
        repository.set(key, "value", Duration.ofMinutes(3));
        assertThat(repository.exists(key)).isTrue();

        repository.delete(key);

        assertThat(repository.exists(key)).isFalse();
        assertThat(repository.get(key)).isEmpty();
    }
}
