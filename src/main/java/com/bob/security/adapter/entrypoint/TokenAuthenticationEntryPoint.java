package com.bob.security.adapter.entrypoint;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.global.exception.exceptions.ApplicationAuthenticationException;
import com.bob.global.exception.response.AuthenticationError;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /* @formatter:off */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        AuthenticationError error = getAuthenticationError(exception);
        setHeader(response, error);
        String body = objectMapper.writeValueAsString(setBody(exception, error));
        response.getWriter().print(body);
    }

    private AuthenticationError getAuthenticationError(AuthenticationException ex) {
        if (ex instanceof ApplicationAuthenticationException exception)
            return exception.getError();

        return AuthenticationError.FAILED_AUTHENTICATION;
    }

    private void setHeader(HttpServletResponse response, AuthenticationError error) {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(error.getStatus().value());
    }

    private Map<String, Object> setBody(AuthenticationException ex, AuthenticationError error) {
        if (ex instanceof ApplicationAuthenticationException exception)
            return createCustomErrorResponse(exception);

        return createDefaultErrorResponse(error);
    }

    private Map<String, Object> createCustomErrorResponse(ApplicationAuthenticationException authException) {
        AuthenticationError error = authException.getError();

        String message = authException.getCustomMessage() != null
            ? authException.getCustomMessage()
            : error.getMessage();

        return Map.of("code", error.getCode(), "message", message);
    }

    private Map<String, Object> createDefaultErrorResponse(AuthenticationError error) {
        return Map.of("code", error.getCode(), "message", error.getMessage());
    }
    /* @formatter:on */
}
