package com.bob.security.adapter.entrypoint;

import static com.bob.global.exception.response.AuthenticationError.FAILED_AUTHENTICATION;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ProblemDetail;
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
        String message = getMessage(exception, error);

        ProblemDetail problemDetail = forStatusAndDetail(error.getStatus(), message);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        setProblemDetailProperties(problemDetail, error.name());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(error.getStatus().value());
        response.getWriter().print(objectMapper.writeValueAsString(problemDetail));
    }

    private AuthenticationError getAuthenticationError(AuthenticationException ex) {
        if (ex instanceof ApplicationAuthenticationException appException)
            return appException.getError();

        return FAILED_AUTHENTICATION;
    }

    private String getMessage(AuthenticationException ex, AuthenticationError error) {
        if (ex instanceof ApplicationAuthenticationException appException) {
            String customMessage = appException.getCustomMessage();
            return customMessage != null ? customMessage : error.getMessage();
        }
        return error.getMessage();
    }

    private static void setProblemDetailProperties(ProblemDetail detail, String title) {
        detail.setTitle(title);
        detail.setProperty("timestamp", LocalDateTime.now());
    }
    /* @formatter:on */
}
