package com.bob.infrastructure.cache.adapter;

import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.member.application.port.out.MemberCachePort;
import com.bob.infrastructure.cache.repository.KeyValueRepository;

@Component
@RequiredArgsConstructor
public class MemberCacheAdapter implements MemberCachePort {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final KeyValueRepository repository;

    @Override
    public void setAuthenticationCode(String email, String code) {
        repository.set(convertKey(email), code, TTL);
    }

    @Override
    public void setAuthenticationSuccess(String email) {
        repository.set(convertKey(email), "success", TTL);
    }

    @Override
    public boolean checkAuthenticationSuccess(String email) {
        Optional<String> status = repository.get(convertKey(email));
        return status.isPresent() && status.get().equals("success");
    }

    @Override
    public Optional<String> get(String email) {
        return repository.get(convertKey(email));
    }

    @Override
    public void delete(String email) {
        repository.delete(convertKey(email));
    }

    private static String convertKey(String email) {
        return "auth:" + email;
    }
}
