package com.bob.security.adapter.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;

@DisplayName("소셜 로그인 실패 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class OAuth2FailureHandlerTest {

    @InjectMocks
    private OAuth2FailureHandler handler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private String baseUrl = "https://base";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "baseUrl", baseUrl);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 소셜_로그인_시_탈퇴한_계정이라면_예외가_발생한다() throws Exception {
        AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_DEACTIVATED_MEMBER) {
        };

        handler.onAuthenticationFailure(request, response, ex);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=IS_DEACTIVATED_MEMBER");
    }

    @Test
    void 소셜_로그인_시_제재된_계정이라면_예외가_발생한다() throws Exception {
        AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_BANNED_MEMBER) {
        };

        handler.onAuthenticationFailure(request, response, ex);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=IS_BANNED_MEMBER");
    }

    @Test
    void 소셜_로그인_시_인증에_실패하면_예외페이지로_리다이렉트_된다() throws Exception {
        AuthenticationException ex = new AuthenticationException("unknown") {
        };

        handler.onAuthenticationFailure(request, response, ex);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=FAILED_AUTHENTICATION");
    }
}
