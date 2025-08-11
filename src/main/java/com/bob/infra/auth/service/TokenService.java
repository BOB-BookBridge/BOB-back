package com.bob.infra.auth.service;

import static com.bob.global.utils.random.RandomUtils.generateCode;
import static com.bob.global.utils.web.CookieUtils.addCookie;
import static com.bob.global.utils.web.CookieUtils.getCookie;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import com.bob.infra.auth.filter.port.AuthRedisPort;
import com.bob.infra.auth.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

  private final JwtProvider jwtProvider;

  private final AuthRedisPort redisPort;

  @Value("${jwt.access-name}")
  private String accessName;

  @Value("${jwt.refresh-name}")
  private String refreshName;

  @Value("${jwt.refresh-token-expire-time}")
  private Long refreshExpireTime;

  public void reIssueTokenProcess(HttpServletRequest request, HttpServletResponse response) {
    final String oldKey = getCookie(request, refreshName);
    final String newRefreshKey = generateCode(32);
    verifyKey(oldKey);

    final String id = redisPort.updateRefreshKey(oldKey, newRefreshKey, null);
    final String newAccessToken = jwtProvider.generateAccessToken(id);
    addCookie(response, accessName, newAccessToken, refreshExpireTime.intValue());
    addCookie(response, refreshName, newRefreshKey, refreshExpireTime.intValue());
  }

  private static void verifyKey(String key) {
    if (key == null) {
      throw new ApplicationAuthenticationException(AuthenticationError.FAILED_GET_AUTHENTICATION_INFORMATION);
    }
  }
}
