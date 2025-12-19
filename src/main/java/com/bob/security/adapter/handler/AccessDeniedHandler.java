package com.bob.security.adapter.handler;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {

        ProblemDetail problemDetail = forStatusAndDetail(FORBIDDEN, "접근 권한이 없습니다.");
        problemDetail.setTitle("ACCESS_DENIED");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(FORBIDDEN.value());
        response.getWriter().print(objectMapper.writeValueAsString(problemDetail));
    }
}
