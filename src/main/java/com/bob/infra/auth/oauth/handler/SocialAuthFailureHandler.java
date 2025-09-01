package com.bob.infra.auth.oauth.handler;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocialAuthFailureHandler implements AuthenticationFailureHandler {

  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Value("${app.base-url}")
  private String baseUrl;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    if (exception instanceof ApplicationAuthenticationException ex) {
      AuthenticationError error = ex.getError();
      authenticationEntryPoint.commence(request, response, new ApplicationAuthenticationException(error) {});
      return;
    }
    response.sendRedirect(baseUrl + "/error?cause=" + exception.getMessage());
  }
}
