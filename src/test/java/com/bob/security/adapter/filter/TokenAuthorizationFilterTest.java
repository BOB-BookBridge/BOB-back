package com.bob.security.adapter.filter;

import static com.bob.global.exception.response.AuthenticationError.ACCESS_TOKEN_EXPIRED;
import static com.bob.global.exception.response.AuthenticationError.AUTHENTICATION_FAILED;
import static com.bob.support.fixture.auth.CookieFixture.ACCESS_TOKEN;
import static com.bob.support.fixture.auth.CookieFixture.defaultAuthCookie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.argThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.security.adapter.entrypoint.TokenAuthenticationEntryPoint;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.registry.OptionalRegistry;
import com.bob.security.config.registry.PermitAllRegistry;
import com.bob.security.model.MemberDetails;

@DisplayName("JWT 토큰 인증 필터 테스트")
@ExtendWith(MockitoExtension.class)
class TokenAuthorizationFilterTest {

    @InjectMocks
    private TokenAuthorizationFilter tokenAuthorizationFilter;

    @Mock
    private TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    @Mock
    private TokenManager tokenManager;

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
    void 토큰_인증() throws Exception {
        given(request.getCookies()).willReturn(new Cookie[] {defaultAuthCookie()});
        given(tokenManager.verify(ACCESS_TOKEN)).willReturn(true);
        given(tokenManager.expire(ACCESS_TOKEN)).willReturn(false);
        given(tokenManager.getClaim(ACCESS_TOKEN)).willReturn(UUID.randomUUID());

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isInstanceOf(MemberDetails.class);

        then(filterChain).should().doFilter(request, response);
    }

    @Test
    void 토큰_인증_실패() throws Exception {
        given(request.getCookies()).willReturn(null);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(tokenAuthenticationEntryPoint).should().commence(eq(request), eq(response),
            argThat(e -> (
                (ApplicationAuthenticationException)e).getError() == AUTHENTICATION_FAILED
            )
        );
    }

    @Test
    void 토큰_검증_실패() throws Exception {
        Cookie cookie = defaultAuthCookie();
        given(request.getCookies()).willReturn(new Cookie[] {cookie});
        given(tokenManager.verify(ACCESS_TOKEN)).willReturn(false);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(tokenAuthenticationEntryPoint).should().commence(eq(request), eq(response),
            argThat(e -> (
                (ApplicationAuthenticationException)e).getError() == AUTHENTICATION_FAILED
            )
        );
    }

    @Test
    void 토큰_만료() throws Exception {
        Cookie cookie = defaultAuthCookie();
        given(request.getCookies()).willReturn(new Cookie[] {cookie});
        given(tokenManager.verify(ACCESS_TOKEN)).willReturn(true);
        given(tokenManager.expire(ACCESS_TOKEN)).willReturn(true);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(tokenAuthenticationEntryPoint).should().commence(eq(request), eq(response),
            argThat(e -> (
                (ApplicationAuthenticationException)e).getError() == ACCESS_TOKEN_EXPIRED
            )
        );
    }

    @Test
    void 모든_요청_접근_허용_url_필터_생략() throws Exception {
        given(permitAllRegistry.isWhiteList(request)).willReturn(true);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(filterChain).should().doFilter(request, response);
    }

    @Test
    void 선택적_인증_쿠키_미존재_시_인증_필터_생략() throws Exception {
        given(optionalRegistry.isOptionalAuth(request)).willReturn(true);
        given(request.getCookies()).willReturn(null);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(filterChain).should().doFilter(request, response);
    }

    @Test
    void 선택적_인증_유효하지_않은_토큰인_경우_쿠키_제거_및_필터_생략() throws Exception {
        Cookie cookie = defaultAuthCookie();
        given(request.getCookies()).willReturn(new Cookie[] {cookie});
        given(optionalRegistry.isOptionalAuth(request)).willReturn(true);
        given(tokenManager.verify(ACCESS_TOKEN)).willReturn(false);

        tokenAuthorizationFilter.doFilterInternal(request, response, filterChain);

        then(response).should().addHeader(eq("Set-Cookie"),
            argThat(value ->
                value.contains("AUTHORIZATION=") && value.contains("Max-Age=0")
            )
        );
        then(response).should().addHeader(eq("Set-Cookie"),
            argThat(value ->
                value.contains("REFRESH_KEY=") && value.contains("Max-Age=0")
            )
        );
        then(filterChain).should().doFilter(request, response);
    }
}
