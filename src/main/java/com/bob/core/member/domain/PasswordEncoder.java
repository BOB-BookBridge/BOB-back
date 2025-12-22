package com.bob.core.member.domain;

public interface PasswordEncoder {

    String encode(String password);

    boolean matches(String password, String passwordHash);
}
