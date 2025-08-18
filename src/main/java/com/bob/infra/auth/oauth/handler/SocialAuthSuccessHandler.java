package com.bob.infra.auth.oauth.handler;

import static com.bob.global.utils.random.RandomUtils.*;
import static com.bob.global.utils.web.CookieUtils.addCookie;

import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.infra.config.props.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialAuthSuccessHandler implements AuthenticationSuccessHandler {

  @Value("${app.base-url}")
  private String baseUrl;

  private final AuthRedisPort redisPort;

  private final JwtProvider jwtProvider;
  private final JwtProperties jwtProps;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    String memberId = authentication.getName();
    String accessToken = jwtProvider.generateAccessToken(memberId);
    String refreshKey = generateCode(32);
    redisPort.updateRefreshKey(null, refreshKey, memberId);
    addCookie(response, jwtProps.accessName(), accessToken, jwtProps.refreshTokenExpireTime());
    addCookie(response, jwtProps.refreshName(), refreshKey, jwtProps.refreshTokenExpireTime());
    response.sendRedirect(baseUrl);
  }
}
