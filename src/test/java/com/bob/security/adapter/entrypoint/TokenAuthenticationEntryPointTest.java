package com.bob.security.adapter.entrypoint;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.exception.response.AuthenticationError.IS_EXPIRED_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(content).contains(FAILED_AUTHENTICATION.getCode());
        assertThat(content).contains(FAILED_AUTHENTICATION.getMessage());
    }

    @Test
    void ApplicationAuthenticationException_발생_시_커스텀_예외_반환() throws IOException {
        AuthenticationException exception = new ApplicationAuthenticationException(IS_EXPIRED_TOKEN) {
        };

        tokenAuthenticationEntryPoint.commence(null, response, exception);

        String content = response.getContentAsString();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(content).contains(IS_EXPIRED_TOKEN.getCode());
        assertThat(content).contains(IS_EXPIRED_TOKEN.getMessage());
    }
}
