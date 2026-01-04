package com.bob.infrastructure.secure.adapter.encoder;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Component;

import com.bob.core.member.domain.encoder.PasswordEncoder;

@Component
public class SecurePasswordEncoder implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder encoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String password, String passwordHash) {
        return encoder.matches(password, passwordHash);
    }
}
