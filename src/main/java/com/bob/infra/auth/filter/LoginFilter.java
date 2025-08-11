package com.bob.infra.auth.filter;

import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.addCookie;
import static com.bob.global.utils.web.CookieUtils.getCookie;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.filter.request.LoginRequest;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.auth.response.MemberDetails;
import com.bob.infra.config.props.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final AuthRedisPort redisPort;
  private final JwtProvider jwtProvider;
  private final JwtProperties jwtProps;
  private final ObjectMapper objectMapper;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    LoginRequest loginRequest = readLoginData(request);
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
    return authenticationManager.authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
    MemberDetails principal = (MemberDetails) authentication.getPrincipal();
    UUID memberId = principal.id();
    String accessToken = jwtProvider.generateAccessToken(memberId.toString());
    String refreshKey = generateCode(32);
    redisPort.updateRefreshKey(getCookie(request, jwtProps.refreshName()), refreshKey, memberId.toString());
    addCookie(response, jwtProps.accessName(), accessToken, jwtProps.refreshTokenExpireTime());
    addCookie(response, jwtProps.refreshName(), refreshKey, jwtProps.refreshTokenExpireTime());
    response.setStatus(HttpStatus.OK.value());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
    authenticationEntryPoint.commence(
        request,
        response,
        new ApplicationAuthenticationException(AuthenticationError.FAILED_AUTHENTICATION) {}
    );
  }

  private LoginRequest readLoginData(HttpServletRequest request) {
    try {
      return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
