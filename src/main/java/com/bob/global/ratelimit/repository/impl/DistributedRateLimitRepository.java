package com.bob.global.ratelimit.repository.impl;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.global.ratelimit.repository.RateLimitRepository;

@Slf4j
@RequiredArgsConstructor
public class DistributedRateLimitRepository implements RateLimitRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String BUCKET_PREFIX = "rate_limit:bucket:";

    @Override
    public boolean isAllowed(String key, long windowSeconds, int maxRequests) {
        String redisKey = BUCKET_PREFIX + key;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount == null) {
            return false;
        }

        if (currentCount == 1) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for key: {}, limit: {}, current: {}", key, maxRequests, currentCount);
            return false;
        }

        return true;
    }

    @Override
    public long getWaitForRefill(String key, long windowSeconds, int maxRequests) {
        String redisKey = BUCKET_PREFIX + key;

        Long ttl = redisTemplate.getExpire(redisKey);

        if (ttl == null || ttl < 0) {
            return 0L;
        }

        return ttl;
    }
}
