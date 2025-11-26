package com.bob.infrastructure.cache.repository;

import java.time.Duration;
import java.util.Optional;

public interface KeyValueRepository {

    void set(String key, String value, Duration ttl);

    Optional<String> get(String key);

    boolean exists(String key);

    void delete(String key);

    void clear();
}
