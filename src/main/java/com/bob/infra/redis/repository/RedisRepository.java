package com.bob.infra.redis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;

  public void setValue(String key, String value, Duration ttl) {
    redisTemplate.opsForValue().set(key, value, ttl);
  }

  public Optional<String> getValue(String key) {
    return isExist(key)
        ? Optional.ofNullable(redisTemplate.opsForValue().get(key))
        : Optional.empty();
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  public boolean isExist(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}
