package com.bob.infra.auth.oauth.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("소셜 로그인 실패 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthFailureHandlerTest {

  @InjectMocks
  private SocialAuthFailureHandler handler;

  @Mock
  private AuthenticationEntryPoint authenticationEntryPoint;

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
  void 탈퇴_계정_소셜_로그인_예외_테스트() throws Exception {
    // given
    AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_REMOVED_MEMBER);

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    ArgumentCaptor<ApplicationAuthenticationException> captor = ArgumentCaptor.forClass(ApplicationAuthenticationException.class);
    then(authenticationEntryPoint).should().commence(any(), any(), captor.capture());

    ApplicationAuthenticationException captured = captor.getValue();
    assertThat(captured).isNotNull();
    assertThat(captured.getError()).isEqualTo(AuthenticationError.IS_REMOVED_MEMBER);
  }

  @Test
  void 제재_계정_소셜_로그인_예외_테스트() throws Exception {
    // given
    AuthenticationException ex = new ApplicationAuthenticationException(AuthenticationError.IS_BANNED_MEMBER);

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    ArgumentCaptor<ApplicationAuthenticationException> captor = ArgumentCaptor.forClass(ApplicationAuthenticationException.class);
    then(authenticationEntryPoint).should().commence(any(), any(), captor.capture());

    ApplicationAuthenticationException captured = captor.getValue();
    assertThat(captured).isNotNull();
    assertThat(captured.getError()).isEqualTo(AuthenticationError.IS_BANNED_MEMBER);
  }

  @Test
  void 처리되지_않은_예외_테스트() throws Exception {
    // given
    AuthenticationException ex = new AuthenticationException("unknown") {};

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    then(authenticationEntryPoint).should(never()).commence(any(), any(), any());
    assertThat(response.getRedirectedUrl()).contains("/error?cause=unknown");
  }
}
