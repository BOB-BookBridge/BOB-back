package com.bob.infra.auth.oauth.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
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

@DisplayName("소셜 로그인 실패 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthFailureHandlerTest {

  @InjectMocks
  private SocialAuthFailureHandler handler;

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
  void 탈퇴_계정_소셜_로그인_예외() throws Exception {
    // given
    AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_WITHDRAWN_MEMBER) {};

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    assertThat(response.getStatus()).isEqualTo(302);
    assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=IS_WITHDRAWN_MEMBER");
  }

  @Test
  void 제재_계정_소셜_로그인_예외() throws Exception {
    // given
    AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_BANNED_MEMBER) {};

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    assertThat(response.getStatus()).isEqualTo(302);
    assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=IS_BANNED_MEMBER");
  }

  @Test
  void 기본_예외() throws Exception {
    // given
    AuthenticationException ex = new AuthenticationException("unknown") {};

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    assertThat(response.getStatus()).isEqualTo(302);
    assertThat(response.getRedirectedUrl()).isEqualTo("https://base/error?cause=FAILED_AUTHENTICATION");
  }
}