package com.bob.global.ratelimit.repository;

public interface RateLimitRepository {

    boolean isAllowed(String key, long windowSeconds, int maxRequests);

    long getWaitForRefill(String key, long windowSeconds, int maxRequests);
}
