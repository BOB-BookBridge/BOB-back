package com.bob.security.adapter.entrypoint;

import static com.bob.global.exception.response.AuthenticationError.ACCESS_TOKEN_EXPIRED;
import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.exception.response.AuthenticationError.LOGIN_RATE_LIMIT_EXCEEDED;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;

@DisplayName("JWT 예외 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class TokenAuthenticationEntryPointTest {

    @InjectMocks
    private TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    private MockHttpServletResponse response;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenAuthenticationEntryPoint, "objectMapper", objectMapper);
        response = new MockHttpServletResponse();
    }

    @Test
    void 일반_인증_예외_발생_시_기본_예외_반환() throws IOException {
        // [요청 예시] POST /api/resource with invalid token
        // [문제 상황] 유효하지 않은 토큰으로 요청
        AuthenticationException exception = new AuthenticationException("") {
        };

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        ProblemDetail problemDetail = objectMapper.readValue(response.getContentAsString(), ProblemDetail.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problemDetail.getDetail()).isEqualTo(FAILED_AUTHENTICATION.getMessage());
        assertThat(problemDetail.getTitle()).isEqualTo(FAILED_AUTHENTICATION.name());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 애플리케이션_인증_예외_발생_시_커스텀_예외_반환() throws IOException {
        // [요청 예시] POST /api/resource with expired token
        // [문제 상황] 만료된 토큰으로 요청
        AuthenticationException exception = new ApplicationAuthenticationException(ACCESS_TOKEN_EXPIRED);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        ProblemDetail problemDetail = objectMapper.readValue(response.getContentAsString(), ProblemDetail.class);
        assertThat(response.getStatus()).isEqualTo(ACCESS_TOKEN_EXPIRED.getStatus().value());
        assertThat(problemDetail.getDetail()).isEqualTo(ACCESS_TOKEN_EXPIRED.getMessage());
        assertThat(problemDetail.getTitle()).isEqualTo(ACCESS_TOKEN_EXPIRED.name());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 로그인_시도_횟수_초과_시_예외_반환() throws IOException {
        // [요청 예시] POST /auth/login (빠른 시간에 5회 이상)
        // [문제 상황] 로그인 시도 횟수 초과
        String message = "로그인 시도가 너무 많습니다. 30초 후 다시 시도해주세요.";
        AuthenticationException exception = new ApplicationAuthenticationException(LOGIN_RATE_LIMIT_EXCEEDED, message);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        ProblemDetail problemDetail = objectMapper.readValue(response.getContentAsString(), ProblemDetail.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(problemDetail.getDetail()).isEqualTo(message);
        assertThat(problemDetail.getTitle()).isEqualTo(LOGIN_RATE_LIMIT_EXCEEDED.name());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 커스텀_메시지_사용() throws IOException {
        // [요청 예시] POST /api/resource with expired token
        // [문제 상황] 토큰이 만료되었으나 커스텀 메시지 반환
        String customMessage = "커스텀 에러 메시지";
        AuthenticationException exception = new ApplicationAuthenticationException(ACCESS_TOKEN_EXPIRED, customMessage);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        ProblemDetail problemDetail = objectMapper.readValue(response.getContentAsString(), ProblemDetail.class);
        assertThat(response.getStatus()).isEqualTo(ACCESS_TOKEN_EXPIRED.getStatus().value());
        assertThat(problemDetail.getDetail()).isEqualTo(customMessage);
        assertThat(problemDetail.getDetail()).doesNotContain(ACCESS_TOKEN_EXPIRED.getMessage());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }
}
