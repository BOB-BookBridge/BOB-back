package com.bob.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bob.support.annotation.ContainerTest;

@DisplayName("로그아웃 테스트")
@ContainerTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "cookie.same-site=LAX")
record LogoutTest(MockMvc mockMvc) {

    @Test
    void 로그아웃() throws Exception {
        // 로그인 API 호출
        String loginRequest = """
            {
                "email": "test@test.com",
                "password": "1234"
            }
            """;

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
            .andExpect(status().isOk())
            .andReturn();

        // 쿠키 검증
        List<String> loginCookies = loginResult.getResponse().getHeaders("Set-Cookie");
        assertThat(loginCookies).hasSize(2);

        String authCookie = loginCookies.stream()
            .filter(cookie -> cookie.startsWith("AUTHORIZATION="))
            .findFirst()
            .orElseThrow();
        assertThat(authCookie)
            .contains("Domain=.bookbridge.kr")
            .contains("HttpOnly")
            .contains("Secure")
            .contains("SameSite=LAX")
            .doesNotContain("Max-Age=0");

        String refreshCookie = loginCookies.stream()
            .filter(cookie -> cookie.startsWith("REFRESH_KEY="))
            .findFirst()
            .orElseThrow();
        assertThat(refreshCookie)
            .contains("Domain=.bookbridge.kr")
            .contains("HttpOnly")
            .contains("Secure")
            .contains("SameSite=LAX")
            .doesNotContain("Max-Age=0");

        // 로그아웃 API 호출
        MvcResult logoutResult = mockMvc.perform(post("/auth/logout")
                .cookie(loginResult.getResponse().getCookies()))
            .andExpect(status().isOk())
            .andReturn();

        // 쿠키 삭제 검증
        List<String> logoutCookies = logoutResult.getResponse().getHeaders("Set-Cookie");
        assertThat(logoutCookies).hasSize(2);

        String removedAuthCookie = logoutCookies.stream()
            .filter(cookie -> cookie.startsWith("AUTHORIZATION="))
            .findFirst()
            .orElseThrow();
        assertThat(removedAuthCookie)
            .contains("Domain=.bookbridge.kr")
            .contains("Max-Age=0")
            .contains("HttpOnly")
            .contains("Secure")
            .contains("SameSite=LAX");

        String removedRefreshCookie = logoutCookies.stream()
            .filter(cookie -> cookie.startsWith("REFRESH_KEY="))
            .findFirst()
            .orElseThrow();
        assertThat(removedRefreshCookie)
            .contains("Domain=.bookbridge.kr")
            .contains("Max-Age=0")
            .contains("HttpOnly")
            .contains("Secure")
            .contains("SameSite=LAX");
    }
}
