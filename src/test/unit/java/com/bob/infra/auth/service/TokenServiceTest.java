package com.bob.infra.auth.service;

import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_NAME;
import static com.bob.support.fixture.auth.CookieFixture.REFRESH_COOKIE_NAME;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("토큰 재발급 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

  @InjectMocks
  private TokenService tokenService;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private AuthRedisPort redisPort;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  private static final String NEW_ACCESS_TOKEN = "new-access-token";
  private static final String OLD_REFRESH_KEY = "old-refresh-key";

  @BeforeEach
  void setupRequestCookie() {
    final Cookie[] cookies = {
        new Cookie(REFRESH_COOKIE_NAME, OLD_REFRESH_KEY)
    };
    given(request.getCookies()).willReturn(cookies);
    setField(tokenService, "accessName", AUTH_COOKIE_NAME);
    setField(tokenService, "refreshName", REFRESH_COOKIE_NAME);
    setField(tokenService, "refreshExpireTime", 600L);
  }

  @Test
  @DisplayName("토큰 재발급 성공 테스트")
  void 쿠키가_존재하면_토큰을_재발급하고_SetCookie헤더를_추가한다() {
    // given
    given(request.getCookies()).willReturn(new Cookie[]{new Cookie("REFRESH_KEY", OLD_REFRESH_KEY)});
    given(redisPort.updateRefreshKey(eq(OLD_REFRESH_KEY), anyString(), eq(null))).willReturn(MEMBER_ID.toString());
    given(jwtProvider.generateAccessToken(MEMBER_ID.toString())).willReturn(NEW_ACCESS_TOKEN);

    // when
    tokenService.reIssueTokenProcess(request, response);

    // then
    verify(redisPort).updateRefreshKey(eq(OLD_REFRESH_KEY), anyString(), eq(null));
    verify(jwtProvider).generateAccessToken(MEMBER_ID.toString());
    verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
  }

  @Test
  @DisplayName("토큰 재발급 실패 테스트 - 쿠키 없음")
  void 쿠키가_없으면_예외가_발생한다() {
    // given
    given(request.getCookies()).willReturn(null);

    // when & then
    assertThatThrownBy(() -> tokenService.reIssueTokenProcess(request, response))
        .isInstanceOf(ApplicationAuthenticationException.class)
        .hasMessage(AuthenticationError.FAILED_GET_AUTHENTICATION_INFORMATION.getMessage());
  }
}
