package com.bob.security.adapter.filter;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.global.exception.response.AuthenticationError.IS_DEACTIVATED_MEMBER;
import static com.bob.support.fixture.auth.CookieFixture.ACCESS_TOKEN;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE_HEADER;
import static com.bob.support.fixture.auth.CookieFixture.SET_COOKIE_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.contains;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.security.adapter.filter.request.LoginRequest;
import com.bob.security.application.port.out.AuthCachePort;
import com.bob.security.application.port.out.TokenManager;
import com.bob.security.config.props.HeaderProperties;
import com.bob.security.model.MemberDetails;

@DisplayName("로그인 필터 테스트")
@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    @InjectMocks
    private LoginFilter loginFilter;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private HeaderProperties headerProperties;

    @Mock
    private AuthCachePort cachePort;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loginFilter, "objectMapper", objectMapper);
    }

    @Test
    void 로그인_인증_시도() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        byte[] bytes = objectMapper.writeValueAsBytes(loginRequest);

        given(request.getInputStream()).willReturn(new DelegatingServletInputStream(new ByteArrayInputStream(bytes)));
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);

        Authentication result = loginFilter.attemptAuthentication(request, response);

        assertThat(result).isNotNull();
        then(authManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void 로그인_인증() {
        FilterChain filterChain = mock(FilterChain.class);
        MemberDetails memberDetails = new MemberDetails(UUID.randomUUID(), true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            memberDetails, null, memberDetails.getAuthorities()
        );
        given(tokenManager.create(any(String.class))).willReturn(ACCESS_TOKEN);
        given(headerProperties.refreshName()).willReturn("REFRESH_COOKIE_NAME");
        given(headerProperties.accessName()).willReturn("AUTHORIZATION");
        given(headerProperties.refreshTokenExpireTime()).willReturn(600L);

        loginFilter.successfulAuthentication(request, response, filterChain, authentication);

        verify(response, times(1)).addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE_HEADER));
        verify(response).setStatus(HttpStatus.OK.value());

        assertThat(authentication.getPrincipal()).isInstanceOf(MemberDetails.class);

        then(cachePort).should(times(1)).updateRefreshKey(any(), any(), any());
    }

    @Test
    void 로그인_인증_실패() throws Exception {
        AuthenticationException failed = mock(AuthenticationException.class);

        loginFilter.unsuccessfulAuthentication(request, response, failed);

        then(authenticationEntryPoint).should().commence(eq(request), eq(response),
            argThat(e -> (
                (ApplicationAuthenticationException)e).getError() == FAILED_AUTHENTICATION
            )
        );
        then(cachePort).shouldHaveNoInteractions();
    }

    @Test
    void 로그인_인증_시_탈퇴한_회원이라면_예외가_발생한다() throws Exception {
        AuthenticationException ex = new DisabledException("disabled");

        loginFilter.unsuccessfulAuthentication(request, response, ex);

        then(authenticationEntryPoint).should().commence(eq(request), eq(response),
            argThat(e -> (
                (ApplicationAuthenticationException)e).getError() == IS_DEACTIVATED_MEMBER
            )
        );
        then(cachePort).shouldHaveNoInteractions();
    }
}
