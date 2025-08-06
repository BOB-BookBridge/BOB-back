package com.bob.infra.auth.jwt.filter;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.exception.response.AuthenticationError.IS_EXPIRED_TOKEN;
import static com.bob.support.fixture.auth.CookieFixture.ACCESS_VALUE;
import static com.bob.support.fixture.auth.CookieFixture.defaultAuthCookie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.argThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.bob.infra.auth.response.MemberDetails;
import com.bob.infra.config.registry.OptionalRegistry;
import com.bob.infra.config.registry.PermitAllRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@DisplayName("JWT 토큰 인증 필터 테스트")
@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

  @InjectMocks
  private JwtAuthorizationFilter jwtAuthorizationFilter;

  @Mock
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private PermitAllRegistry permitAllRegistry;

  @Mock
  private OptionalRegistry optionalRegistry;

  @BeforeEach
  void setUp() {
    given(permitAllRegistry.isWhiteList(any(HttpServletRequest.class))).willReturn(false);
  }

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Token 인증 - 성공 테스트")
  void 토큰이_유효하다면_인증에_성공한다() throws Exception {
    // given
    given(request.getCookies()).willReturn(new Cookie[]{defaultAuthCookie()});
    given(jwtProvider.isVerified(ACCESS_VALUE)).willReturn(true);
    given(jwtProvider.isExpired(ACCESS_VALUE)).willReturn(false);
    given(jwtProvider.getMemberId(ACCESS_VALUE)).willReturn(UUID.randomUUID());

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isInstanceOf(MemberDetails.class);
    then(filterChain).should().doFilter(request, response);
  }

  @Test
  @DisplayName("Token 인증 - 실패 테스트(Null Token)")
  void 토큰이_없는_경우_인증에_실패한다() throws Exception {
    // given
    given(request.getCookies()).willReturn(null);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(jwtAuthenticationEntryPoint).should().commence(eq(request), eq(response),
        argThat(e -> ((ApplicationAuthenticationException) e).getError() == FAILED_AUTHENTICATION)
    );
  }

  @Test
  @DisplayName("Token 인증 - 실패 테스트(Token 검증 실패)")
  void 토큰이_검증되지_않으면_인증에_실패한다() throws Exception {
    // given
    Cookie invalidCookie = defaultAuthCookie();
    given(request.getCookies()).willReturn(new Cookie[]{invalidCookie});
    given(jwtProvider.isVerified(ACCESS_VALUE)).willReturn(false);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(jwtAuthenticationEntryPoint).should().commence(eq(request), eq(response),
        argThat(e -> ((ApplicationAuthenticationException) e).getError() == FAILED_AUTHENTICATION)
    );
  }

  @Test
  @DisplayName("Token 인증 - 실패 테스트(Token 만료)")
  void 토큰이_만료되면_인증에_실패한다() throws Exception {
    // given
    Cookie expiredCookie = defaultAuthCookie();
    given(request.getCookies()).willReturn(new Cookie[]{expiredCookie});
    given(jwtProvider.isVerified(ACCESS_VALUE)).willReturn(true);
    given(jwtProvider.isExpired(ACCESS_VALUE)).willReturn(true);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(jwtAuthenticationEntryPoint).should().commence(eq(request), eq(response),
        argThat(e -> ((ApplicationAuthenticationException) e).getError() == IS_EXPIRED_TOKEN)
    );
  }

  @Test
  @DisplayName("화이트리스트 요청 테스트")
  void 화이트리스트_요청은_필터를_통과시킨다() throws Exception {
    // given
    given(permitAllRegistry.isWhiteList(request)).willReturn(true);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(filterChain).should().doFilter(request, response);
  }

  @Test
  @DisplayName("선택적 인증 요청 테스트 - 쿠키 없음")
  void 선택적_인증이고_쿠키가_없으면_필터를_우회한다() throws Exception {
    // given
    given(optionalRegistry.isOptionalAuth(request)).willReturn(true);
    given(request.getCookies()).willReturn(null);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(filterChain).should().doFilter(request, response);
  }

  @Test
  @DisplayName("선택적 인증 요청 테스트 - 유효하지 않은 토큰이면 쿠키를 제거하고 필터를 우회한다")
  void 선택적_인증이고_토큰이_유효하지_않으면_쿠키를_제거하고_필터를_우회한다() throws Exception {
    // given
    Cookie cookie = defaultAuthCookie();
    given(request.getCookies()).willReturn(new Cookie[]{cookie});
    given(optionalRegistry.isOptionalAuth(request)).willReturn(true);
    given(jwtProvider.isVerified(ACCESS_VALUE)).willReturn(false);

    // when
    jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

    // then
    then(response).should().addHeader(eq("Set-Cookie"), argThat(value ->
        value.contains("AUTHORIZATION=") && value.contains("Max-Age=0")
    ));
    then(response).should().addHeader(eq("Set-Cookie"), argThat(value ->
        value.contains("REFRESH_KEY=") && value.contains("Max-Age=0")
    ));
    then(filterChain).should().doFilter(request, response);
  }
}
