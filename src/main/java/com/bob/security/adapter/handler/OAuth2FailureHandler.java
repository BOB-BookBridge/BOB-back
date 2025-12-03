package com.bob.security.adapter.handler;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        final AuthenticationError error = (exception instanceof ApplicationAuthenticationException appEx)
            ? appEx.getError()
            : AuthenticationError.AUTHENTICATION_FAILED;

        response.sendRedirect(UriComponentsBuilder.fromUriString(baseUrl)
            .path("/error")
            .queryParam("cause", error.name())
            .build(true)
            .toUriString()
        );
    }
}
