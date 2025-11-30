package com.bob.global.ratelimit.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.bob.support.annotation.ContainerTest;

@DisplayName("처리 제한기 Aspect 테스트")
@ContainerTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "rate-limiter.distributed=true",
    "rate-limiter.global=true"
})
class RateLimiterAspectTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void 커스텀_제한_요청_허용() {
        for (int i = 0; i < 3; i++) {
            assertThat(mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.1"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.message").asString().isEqualTo("success");
        }
    }

    @Test
    void 커스텀_제한_서로_다른_IP_독립적_허용() {
        for (int i = 0; i < 3; i++) {
            assertThat(mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.2"))
                .hasStatusOk();
        }

        assertThat(mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.3"))
            .hasStatusOk();
    }

    @Test
    void 제한_비활성화_요청_무제한_허용() {
        for (int i = 0; i < 100; i++) {
            assertThat(mvc.post().uri("/auth/no-rate-limit").header("X-Forwarded-For", "10.0.0.4"))
                .hasStatusOk();
        }
    }

    @Test
    void 전역_제한_요청_허용() {
        for (int i = 0; i < 10; i++) {
            assertThat(mvc.post().uri("/auth/global-limit").header("X-Forwarded-For", "10.0.0.6"))
                .hasStatusOk();
        }
    }

    @Test
    void 커스텀_제한_초과_시_예외가_발생한다() {
        for (int i = 0; i < 3; i++) {
            assertThat(mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.5"))
                .hasStatusOk();
        }

        var response = mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.5");

        assertThat(response)
            .hasStatus(HttpStatus.TOO_MANY_REQUESTS)
            .matches(result -> {
                String retryAfter = result.getResponse().getHeader("Retry-After");
                assertThat(retryAfter).isNotNull();
                assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);
                return true;
            })
            .bodyJson()
            .extractingPath("$.message").asString().contains("요청 한도를 초과했습니다");
    }

    @Test
    void 전역_제한_초과_시_예외가_발생한다() {
        for (int i = 0; i < 10; i++) {
            assertThat(mvc.post().uri("/auth/global-limit").header("X-Forwarded-For", "10.0.0.7"))
                .hasStatusOk();
        }

        assertThat(mvc.post().uri("/auth/global-limit").header("X-Forwarded-For", "10.0.0.7"))
            .hasStatus(HttpStatus.TOO_MANY_REQUESTS)
            .matches(result -> {
                String retryAfter = result.getResponse().getHeader("Retry-After");
                assertThat(retryAfter).isNotNull();
                assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);
                return true;
            })
            .bodyJson()
            .extractingPath("$.message").asString().contains("요청 한도를 초과했습니다");
    }

    @Test
    void 제한_초과_시_Retry_After_헤더_반환() {
        for (int i = 0; i < 3; i++) {
            assertThat(mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.8"))
                .hasStatusOk();
        }

        var response = mvc.post().uri("/auth/rate-limit").header("X-Forwarded-For", "10.0.0.8");
        assertThat(response)
            .hasStatus(HttpStatus.TOO_MANY_REQUESTS)
            .matches(result -> {
                String retryAfter = result.getResponse().getHeader("Retry-After");
                assertThat(retryAfter).isNotNull();
                assertThat(Long.parseLong(retryAfter)).isGreaterThan(0);
                return true;
            });
    }
}
