package com.bob.support.fixture.auth;

import jakarta.servlet.http.Cookie;

public class CookieFixture {

    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String AUTH_COOKIE_HEADER = "AUTHORIZATION=access-token";

    public static final String AUTH_COOKIE_NAME = "AUTHORIZATION";
    public static final String REFRESH_COOKIE_NAME = "REFRESH_KEY";

    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_KEY = "refresh-key";

    public static Cookie defaultAuthCookie() {
        return new Cookie(AUTH_COOKIE_NAME, ACCESS_TOKEN);
    }

    public static Cookie nonMatchingCookie() {
        return new Cookie("otherName", "otherValue");
    }
}
