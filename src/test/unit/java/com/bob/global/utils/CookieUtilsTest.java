package com.bob.global.utils;

import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_ACCESS_VALUE;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_NAME;
import static com.bob.support.fixture.auth.CookieFixture.SET_COOKIE_HEADER;
import static com.bob.support.fixture.auth.CookieFixture.defaultAuthCookie;
import static com.bob.support.fixture.auth.CookieFixture.nonMatchingCookie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.contains;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.bob.global.utils.web.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cookie 유틸 테스트")
class CookieUtilsTest {

  @Test
  @DisplayName("Cookie 조회 - 성공 테스트")
  void 쿠키를_정상적으로_조회할_수_있다() {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getCookies()).willReturn(new Cookie[]{defaultAuthCookie()});

    // when
    String result = CookieUtils.getCookie(request, AUTH_COOKIE_NAME);

    // then
    assertThat(result).isEqualTo(AUTH_COOKIE_ACCESS_VALUE);
  }

  @Test
  @DisplayName("Cookie 조회 - 실패 테스트(Null Cookie)")
  void 쿠키가_없으면_null을_반환한다() {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getCookies()).willReturn(null);

    // when
    String result = CookieUtils.getCookie(request, AUTH_COOKIE_NAME);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Cookie 조회 - 실패 테스트(이름 불일치)")
  void 쿠키_이름이_다르면_null을_반환한다() {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getCookies()).willReturn(new Cookie[]{defaultAuthCookie()});

    // when
    String result = CookieUtils.getCookie(request, nonMatchingCookie().getName());

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Cookie 헤더 추가 - 성공 테스트")
  void 쿠키를_응답에_추가할_수_있다() {
    // given
    HttpServletResponse response = mock(HttpServletResponse.class);

    // when
    CookieUtils.addCookie(response, AUTH_COOKIE_NAME, AUTH_COOKIE_ACCESS_VALUE, 3600);

    // then
    then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE));
  }

  @Test
  @DisplayName("Cookie 제거 - 성공 테스트")
  void 쿠키를_응답에서_제거할_수_있다() {
    // given
    HttpServletResponse response = mock(HttpServletResponse.class);

    // when
    CookieUtils.removeCookie(response, AUTH_COOKIE_NAME);

    // then
    then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE_NAME + "="));
    then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains("Max-Age=0"));
  }
}
