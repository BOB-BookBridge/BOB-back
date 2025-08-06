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
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

  private final JwtProvider jwtProvider;

  private final AuthRedisPort redisPort;

  private static final String ACCESS_COOKIE_NAME = "AUTHORIZATION";
  private static final String REFRESH_COOKIE_NAME = "REFRESH_KEY";

  public void reIssueTokenProcess(HttpServletRequest request, HttpServletResponse response) {
    String oldKey = getCookie(request, REFRESH_COOKIE_NAME);
    verifyKey(oldKey);
    String newRefreshKey = generateCode(32);
    String id = redisPort.updateRefreshKey(oldKey, newRefreshKey, null);
    String newAccessToken = jwtProvider.generateAccessToken(id);
    addCookie(response, ACCESS_COOKIE_NAME, newAccessToken, 7200);
    addCookie(response, REFRESH_COOKIE_NAME, newRefreshKey, 1209600);
  }

  private void verifyKey(String key) {
    if (key == null) {
      throw new ApplicationAuthenticationException(AuthenticationError.FAILED_GET_AUTHENTICATION_INFORMATION);
    }
  }
}
