package com.bob.global.ratelimit.repository.impl;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

import com.bob.global.ratelimit.repository.RateLimitRepository;

@Slf4j
public class MemoryRateLimitRepository implements RateLimitRepository {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String key, long windowSeconds, int maxRequests) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(windowSeconds, maxRequests));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            log.warn(
                "Rate limit exceeded for key: {}, limit: {}, remaining: {}",
                key, maxRequests, probe.getRemainingTokens()
            );
        }

        return probe.isConsumed();
    }

    @Override
    public long getWaitForRefill(String key, long windowSeconds, int maxRequests) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(windowSeconds, maxRequests));
        EstimationProbe estimationProbe = bucket.estimateAbilityToConsume(1);

        if (estimationProbe.canBeConsumed())
            return 0L;

        long nanos = estimationProbe.getNanosToWaitForRefill();
        return (nanos + 999_999_999) / 1_000_000_000;
    }

    private Bucket createBucket(long windowSeconds, int maxRequests) {
        Bandwidth limit = Bandwidth.classic(
            maxRequests,
            Refill.intervally(maxRequests, Duration.ofSeconds(windowSeconds))
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
