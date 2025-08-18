package com.bob.infra.auth.oauth.handler;

import static com.bob.global.utils.web.CookieUtils.removeCookie;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialAuthFailureHandler implements AuthenticationFailureHandler {

  private static final String ACCESS_COOKIE_NAME = "AUTHORIZATION";
  private static final String REFRESH_COOKIE_NAME = "REFRESH_KEY";

  @Value("${app.base-url}")
  private String baseUrl;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    exception.printStackTrace();
    removeCookie(response, ACCESS_COOKIE_NAME);
    removeCookie(response, REFRESH_COOKIE_NAME);
    response.sendRedirect(baseUrl + "/error");
  }
}
