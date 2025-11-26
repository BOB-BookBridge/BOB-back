package com.bob.infrastructure.cache.adapter;

import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.infrastructure.cache.repository.KeyValueRepository;
import com.bob.security.application.port.out.AuthCachePort;

@Component
@RequiredArgsConstructor
public class AuthCacheAdapter implements AuthCachePort {

    private static final Duration TTL = Duration.ofDays(14);

    private final KeyValueRepository repository;

    @Override
    public void setRefreshKey(String current, String value) {
        repository.set(convertKey(current), value, TTL);
    }

    @Override
    public void updateRefreshKey(String old, String current, String value) {
        repository.delete(convertKey(old));
        repository.set(convertKey(current), value, TTL);
    }

    @Override
    public Optional<String> get(String refresh) {
        return repository.get(convertKey(refresh));
    }

    private String convertKey(String refresh) {
        return "refresh:" + refresh;
    }
}
