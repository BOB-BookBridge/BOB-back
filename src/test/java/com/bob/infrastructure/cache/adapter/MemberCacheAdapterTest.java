package com.bob.infrastructure.cache.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.infrastructure.cache.repository.KeyValueRepository;
import com.bob.support.annotation.ContainerTest;

@ContainerTest
@DisplayName("회원 캐시 활용 테스트")
record MemberCacheAdapterTest(MemberCacheAdapter adapter, KeyValueRepository repository) {

    @AfterEach
    void tearDown() {
        repository.clear();
    }

    @Test
    void 인증_코드_저장() {
        String email = "test@example.com";

        adapter.setAuthenticationCode(email, "000000");

        assertThat(repository.exists("auth:test@example.com")).isTrue();
        assertThat(repository.get(convertKey(email))).isEqualTo(Optional.of("000000"));
        assertThat(adapter.checkAuthenticationSuccess(email)).isFalse();
    }

    @Test
    void 인증_성공_저장() {
        String email = "test@example.com";

        adapter.setAuthenticationSuccess(email);

        assertThat(repository.exists("auth:test@example.com")).isTrue();
        assertThat(repository.get(convertKey(email))).isEqualTo(Optional.of("success"));
    }

    @Test
    void 인증_여부_검증() {
        String email = "test@example.com";
        adapter.setAuthenticationSuccess(email);

        boolean check = adapter.checkAuthenticationSuccess(email);

        assertThat(check).isTrue();

        // 값 삭제 후 검증 실패 확인
        adapter.delete(email);

        check = adapter.checkAuthenticationSuccess(email);

        assertThat(check).isFalse();
    }

    @Test
    void 조회() {
        String email = "test@example.com";
        adapter.setAuthenticationSuccess(email);

        Optional<String> result = adapter.get(email);

        assertThat(result).isEqualTo(Optional.of("success"));
    }

    @Test
    void 삭제() {
        String email = "test@example.com";
        adapter.setAuthenticationSuccess(email);

        adapter.delete(email);

        assertThat(repository.get(convertKey(email))).isEmpty();
    }

    private String convertKey(String email) {
        return "auth:" + email;
    }
}
