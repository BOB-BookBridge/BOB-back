package com.bob.infra.auth.oauth.handler;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SocialAuthFailureHandler implements AuthenticationFailureHandler {

  @Value("${app.base-url}")
  private String baseUrl;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
    final AuthenticationError error = (exception instanceof ApplicationAuthenticationException appEx)
        ? appEx.getError()
        : AuthenticationError.FAILED_AUTHENTICATION;

    response.sendRedirect(UriComponentsBuilder.fromUriString(baseUrl)
        .path("/error")
        .queryParam("cause", error.name())
        .build(true)
        .toUriString()
    );
  }
}
