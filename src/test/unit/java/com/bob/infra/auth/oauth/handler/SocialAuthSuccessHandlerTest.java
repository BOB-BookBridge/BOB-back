package com.bob.infra.auth.oauth.handler;

import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_ACCESS_VALUE;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_NAME;
import static com.bob.support.fixture.auth.CookieFixture.REFRESH_COOKIE_NAME;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.config.props.JwtProperties;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("소셜 로그인 성공 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
class SocialAuthSuccessHandlerTest {

  @InjectMocks
  private SocialAuthSuccessHandler handler;

  @Mock
  private AuthRedisPort redisPort;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private JwtProperties jwtProps;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  private String baseUrl = "https://base";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(handler, "baseUrl", baseUrl);
    given(jwtProps.accessName()).willReturn(AUTH_COOKIE_NAME);
    given(jwtProps.refreshName()).willReturn(REFRESH_COOKIE_NAME);
    given(jwtProps.refreshTokenExpireTime()).willReturn(1);
    given(jwtProvider.generateAccessToken(MEMBER_ID.toString())).willReturn(AUTH_COOKIE_ACCESS_VALUE);

    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  @DisplayName("소셜 로그인 성공 - JWT 쿠키 발급, Redis 저장, 리다이렉트")
  void 소셜로그인_성공시_token을_발급하고_홈페이지로_리다이렉트된다() throws Exception {
    // given
    String memberId = MEMBER_ID.toString();
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(MEMBER_ID.toString(), null);

    // when
    handler.onAuthenticationSuccess(request, response, authentication);

    // then
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    then(redisPort).should(times(1)).updateRefreshKey(isNull(), captor.capture(), eq(memberId));

    String issuedRefreshKey = captor.getValue();
    assertThat(issuedRefreshKey).isNotBlank();
    assertThat(issuedRefreshKey.length()).isEqualTo(32);

    Cookie[] cookies = response.getCookies();
    assertThat(cookies).isNotNull();
    assertThat(Arrays.stream(cookies).map(Cookie::getName)).containsExactlyInAnyOrder(AUTH_COOKIE_NAME, REFRESH_COOKIE_NAME);

    Cookie accessCookie = Arrays.stream(cookies).filter(c -> AUTH_COOKIE_NAME.equals(c.getName())).findFirst().orElseThrow();
    Cookie refreshCookie = Arrays.stream(cookies).filter(c -> REFRESH_COOKIE_NAME.equals(c.getName())).findFirst().orElseThrow();
    assertThat(accessCookie.getValue()).isEqualTo(AUTH_COOKIE_ACCESS_VALUE);
    assertThat(refreshCookie.getValue()).isEqualTo(issuedRefreshKey);
    assertThat(response.getRedirectedUrl()).isEqualTo(baseUrl);
  }

  @Test
  @DisplayName("소셜 로그인 성공 - 토큰 생성")
  void 소셜로그인_성공시_access_token을_생성한다() throws IOException {
    // given
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(MEMBER_ID.toString(), null);

    // when
    handler.onAuthenticationSuccess(request, response, auth);

    // then
    then(jwtProvider).should(times(1)).generateAccessToken(MEMBER_ID.toString());
  }
}