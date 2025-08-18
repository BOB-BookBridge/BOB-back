package com.bob.infra.auth.oauth.handler;

import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_NAME;
import static com.bob.support.fixture.auth.CookieFixture.REFRESH_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
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
  @DisplayName("소셜 로그인 실패 - 에러 페이지 리다이렉트")
  void 소셜로그인_실패시_에러페이지로_리다이렉트한다() throws Exception {
    // given
    AuthenticationException ex = new AuthenticationException("oauth failed") {};

    // when
    handler.onAuthenticationFailure(request, response, ex);

    // then
    assertThat(response.getRedirectedUrl()).isEqualTo(baseUrl + "/error");
  }

  @Test
  @DisplayName("소셜 로그인 실패 - 쿠키 삭제")
  void 소셜로그인_실패시_쿠키를_삭제한다() throws Exception {
    // when
    handler.onAuthenticationFailure(request, response, new AuthenticationException("any") {});

    // then
    Cookie[] cookies = response.getCookies();
    assertThat(cookies).isNotNull();

    Cookie accessCookie = Arrays.stream(cookies).filter(c -> AUTH_COOKIE_NAME.equals(c.getName())).findFirst().orElse(null);
    Cookie refreshCookie = Arrays.stream(cookies).filter(c -> REFRESH_COOKIE_NAME.equals(c.getName())).findFirst().orElse(null);
    assertThat(accessCookie).isNotNull();
    assertThat(refreshCookie).isNotNull();
    assertThat(accessCookie.getMaxAge()).isZero();
    assertThat(refreshCookie.getMaxAge()).isZero();
  }
}