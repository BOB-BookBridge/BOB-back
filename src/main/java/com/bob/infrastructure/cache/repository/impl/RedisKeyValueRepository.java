package com.bob.infrastructure.cache.repository.impl;

import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bob.infrastructure.cache.repository.KeyValueRepository;

@Repository
@RequiredArgsConstructor
public class RedisKeyValueRepository implements KeyValueRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public Optional<String> get(String key) {
        return exists(key)
            ? Optional.ofNullable(redisTemplate.opsForValue().get(key))
            : Optional.empty();
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
