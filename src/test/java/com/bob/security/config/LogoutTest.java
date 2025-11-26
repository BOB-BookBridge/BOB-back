package com.bob.security.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.bob.support.annotation.ContainerTest;

@DisplayName("로그아웃 테스트")
@ContainerTest
@AutoConfigureMockMvc
class LogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 로그아웃() throws Exception {
        Cookie jsessionCookie = new Cookie("JSESSIONID", "fake-session");
        Cookie authCookie = new Cookie("AUTHORIZATION", "fake-access-token");
        Cookie refreshCookie = new Cookie("REFRESH", "fake-refresh-token");

        mockMvc.perform(post("/auth/logout")
                .cookie(jsessionCookie, authCookie, refreshCookie))
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge("JSESSIONID", 0))
            .andExpect(cookie().maxAge("AUTHORIZATION", 0))
            .andExpect(cookie().maxAge("REFRESH_KEY", 0));
    }
}
