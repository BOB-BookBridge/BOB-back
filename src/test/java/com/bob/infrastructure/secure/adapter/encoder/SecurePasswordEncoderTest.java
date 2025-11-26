package com.bob.infrastructure.secure.adapter.encoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SecurePasswordEncoderTest {

    @Test
    void 비밀번호_암호화_및_일치_검증() {
        SecurePasswordEncoder securePasswordEncoder = new SecurePasswordEncoder();

        String passwordHash = securePasswordEncoder.encode("secret");

        assertThat(securePasswordEncoder.matches("secret", passwordHash)).isTrue();
        assertThat(securePasswordEncoder.matches("wrong", passwordHash)).isFalse();
    }
}
