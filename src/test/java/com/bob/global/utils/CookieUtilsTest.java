package com.bob.global.utils;

import static com.bob.support.fixture.auth.CookieFixture.ACCESS_TOKEN;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_HEADER;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.utils.web.CookieUtils;

@DisplayName("쿠키 유틸 테스트")
class CookieUtilsTest {

    @Test
    void 쿠키_조회() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getCookies()).willReturn(new Cookie[] {defaultAuthCookie()});

        String result = CookieUtils.getCookie(request, AUTH_COOKIE_NAME);

        assertThat(result).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    void 존재하지_않는_쿠키_조회() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getCookies()).willReturn(new Cookie[] {defaultAuthCookie()});

        String result = CookieUtils.getCookie(request, nonMatchingCookie().getName());

        assertThat(result).isNull();
    }

    @Test
    void 쿠키_헤더_추가() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        CookieUtils.addCookie(response, AUTH_COOKIE_NAME, ACCESS_TOKEN, 3600L);

        then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE_HEADER));
    }

    @Test
    void 쿠키_헤더_제거() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        CookieUtils.removeCookie(response, AUTH_COOKIE_NAME);

        then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE_NAME + "="));
        then(response).should().addHeader(eq(SET_COOKIE_HEADER), contains("Max-Age=0"));
    }
}
