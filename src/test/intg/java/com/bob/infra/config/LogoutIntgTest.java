package com.bob.infra.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.support.TestContainerSupport;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("로그아웃 테스트")
@AutoConfigureMockMvc
@SpringBootTest
class LogoutIntgTest extends TestContainerSupport {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("로그아웃_성공_테스트")
  void 로그아웃_요청_시_세션_무효화_및_쿠키가_삭제된다() throws Exception {
    // given: 가짜 세션 및 쿠키
    Cookie jsessionCookie = new Cookie("JSESSIONID", "fake-session");
    Cookie authCookie = new Cookie("AUTHORIZATION", "fake-access-token");
    Cookie refreshCookie = new Cookie("REFRESH", "fake-refresh-token");

    // when & then
    mockMvc.perform(post("/auth/logout")
            .cookie(jsessionCookie, authCookie, refreshCookie))
        .andExpect(status().isOk())
        .andExpect(cookie().maxAge("JSESSIONID", 0))
        .andExpect(cookie().maxAge("AUTHORIZATION", 0))
        .andExpect(cookie().maxAge("REFRESH_KEY", 0));
  }
}
