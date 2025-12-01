package com.bob.global.utils.web;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.Setter;

import org.springframework.http.ResponseCookie;

public class CookieUtils {

    @Setter
    private static String sameSite = "LAX";

    public static String getCookie(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getCookies())
            .flatMap(cookies -> Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(name, cookie.getName()))
                .map(Cookie::getValue)
                .findAny()).orElse(null);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, Long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
            .path("/")
            .domain(".bookbridge.kr")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(true)
            .maxAge(maxAge)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void removeCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
            .path("/")
            .domain(".bookbridge.kr")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(true)
            .maxAge(0)
            .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
