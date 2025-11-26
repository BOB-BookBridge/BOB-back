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
        setHeader(response);
        String body = objectMapper.writeValueAsString(setBody(exception));
        response.getWriter().print(body);
    }
    /* @formatter:on */

    private void setHeader(HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private Map<String, Object> setBody(AuthenticationException ex) {
        if (ex instanceof ApplicationAuthenticationException exception)
            return createCustomErrorResponse(exception);

        return createDefaultErrorResponse();
    }

    private Map<String, Object> createCustomErrorResponse(ApplicationAuthenticationException authException) {
        AuthenticationError error = authException.getError();
        return Map.of("code", error.getCode(), "message", error.getMessage());
    }

    private Map<String, Object> createDefaultErrorResponse() {
        AuthenticationError error = AuthenticationError.FAILED_AUTHENTICATION;
        return Map.of("code", error.getCode(), "message", error.getMessage());
    }
}
