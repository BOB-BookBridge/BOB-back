package com.bob.infra.auth.filter;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static com.bob.support.fixture.auth.CookieFixture.ACCESS_VALUE;
import static com.bob.support.fixture.auth.CookieFixture.AUTH_COOKIE;
import static com.bob.support.fixture.auth.CookieFixture.SET_COOKIE_HEADER;
import static com.bob.support.fixture.request.LoginRequestFixture.defaultLoginRequest;
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

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.filter.request.LoginRequest;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.auth.response.MemberDetails;
import com.bob.infra.config.props.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.UUID;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.util.ReflectionTestUtils;

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
  private JwtProvider jwtProvider;

  @Mock
  private JwtProperties jwtProps;

  @Mock
  private AuthRedisPort redisPort;

  @Mock
  private Authentication authentication;

  @Mock
  private HttpServletResponse response;

  @Mock
  private HttpServletRequest request;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(loginFilter, "objectMapper", objectMapper);
  }

  @Test
  @DisplayName("로그인 요청 - 성공 테스트")
  void 로그인_요청을_시도할_수_있다() throws Exception {
    // given
    LoginRequest loginRequest = defaultLoginRequest();
    byte[] loginRequestBytes = objectMapper.writeValueAsBytes(loginRequest);

    given(request.getInputStream()).willReturn(new DelegatingServletInputStream(new ByteArrayInputStream(loginRequestBytes)));
    given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);

    // when
    Authentication result = loginFilter.attemptAuthentication(request, response);

    // then
    assertThat(result).isNotNull();
    then(authManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("로그인 인증 - 성공 테스트")
  void 인증에_성공하면_토큰을_발급하고_Security_Context에_정보를_저장한다() {
    // given
    FilterChain filterChain = mock(FilterChain.class);
    MemberDetails memberDetails = new MemberDetails(UUID.randomUUID());
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        memberDetails, null, memberDetails.getAuthorities()
    );
    given(jwtProvider.generateAccessToken(any(String.class))).willReturn(ACCESS_VALUE);
    given(jwtProps.refreshName()).willReturn("REFRESH_COOKIE_NAME");
    given(jwtProps.accessName()).willReturn("AUTHORIZATION");
    given(jwtProps.refreshTokenExpireTime()).willReturn(600);

    // when
    loginFilter.successfulAuthentication(request, response, filterChain, authentication);

    // then
    verify(response, times(1)).addHeader(eq(SET_COOKIE_HEADER), contains(AUTH_COOKIE));
    verify(response).setStatus(HttpStatus.OK.value());
    assertThat(authentication.getPrincipal()).isInstanceOf(MemberDetails.class);
    then(redisPort).should(times(1)).updateRefreshKey(any(), any(), any());
  }

  @Test
  @DisplayName("로그인 인증 - 실패 테스트")
  void 인증에_실패하면_예외_핸들러를_호출한다() throws Exception {
    // given
    AuthenticationException failed = mock(AuthenticationException.class);

    // when
    loginFilter.unsuccessfulAuthentication(request, response, failed);

    // then
    then(authenticationEntryPoint).should().commence(eq(request), eq(response),
        argThat(e -> ((ApplicationAuthenticationException) e).getError() == FAILED_AUTHENTICATION)
    );
    then(redisPort).shouldHaveNoInteractions();
  }
}
