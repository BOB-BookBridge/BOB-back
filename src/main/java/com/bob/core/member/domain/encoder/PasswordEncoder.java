package com.bob.core.member.domain.encoder;

public interface PasswordEncoder {

    String encode(String password);

    boolean matches(String password, String passwordHash);
}
