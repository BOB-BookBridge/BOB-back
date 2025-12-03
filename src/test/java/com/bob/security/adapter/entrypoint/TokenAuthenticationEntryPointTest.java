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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;

@DisplayName("JWT 예외 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class TokenAuthenticationEntryPointTest {

    @InjectMocks
    private TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    private MockHttpServletResponse response;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenAuthenticationEntryPoint, "objectMapper", objectMapper);
        response = new MockHttpServletResponse();
    }

    @Test
    void AuthenticationException_발생_시_기본_예외_반환() throws IOException {
        AuthenticationException exception = new AuthenticationException("") {
        };

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        String content = response.getContentAsString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(content).contains(FAILED_AUTHENTICATION.getMessage());
    }

    @Test
    void ApplicationAuthenticationException_발생_시_커스텀_예외_반환() throws IOException {
        AuthenticationException exception = new ApplicationAuthenticationException(ACCESS_TOKEN_EXPIRED);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        String content = response.getContentAsString();
        assertThat(response.getStatus()).isEqualTo(ACCESS_TOKEN_EXPIRED.getStatus().value());
        assertThat(content).contains(ACCESS_TOKEN_EXPIRED.getMessage());
    }

    @Test
    void 요청_횟수_제한_초과_시_예외가_발생한다() throws IOException {
        String customMessage = "로그인 시도가 너무 많습니다. 30초 후 다시 시도해주세요.";
        AuthenticationException exception = new ApplicationAuthenticationException(LOGIN_RATE_LIMIT_EXCEEDED,
            customMessage);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        String content = response.getContentAsString();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(content).contains(customMessage);
    }

    @Test
    void ApplicationAuthenticationException_커스텀_메시지_사용() throws IOException {
        String customMessage = "커스텀 에러 메시지";
        AuthenticationException exception = new ApplicationAuthenticationException(ACCESS_TOKEN_EXPIRED, customMessage);

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        String content = response.getContentAsString();
        assertThat(response.getStatus()).isEqualTo(ACCESS_TOKEN_EXPIRED.getStatus().value());
        assertThat(content).contains(customMessage);
        assertThat(content).doesNotContain(ACCESS_TOKEN_EXPIRED.getMessage());
    }
}
