package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.SERVER_ERROR;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.ratelimit.exception.RateLimitExceededException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleServerException(Exception ex) {
        ProblemDetail problemDetail = forStatusAndDetail(SERVER_ERROR.getStatus(), SERVER_ERROR.getMessage());
        return setProblemDetailProperties(problemDetail, SERVER_ERROR.name());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String detailMessage = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .findFirst()
            .orElse("제약 조건 위반입니다.");

        ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
        return setProblemDetailProperties(problemDetail, "CONSTRAINT_VIOLATION");
    }

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException ex) {
        ApplicationError error = ex.getError();
        ProblemDetail problemDetail = forStatusAndDetail(error.getStatus(), ex.getMessage());
        return setProblemDetailProperties(problemDetail, error.name());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimitExceeded(RateLimitExceededException ex, HttpServletResponse response) {
        response.addHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        return setProblemDetailProperties(problemDetail, "RATE_LIMIT_EXCEEDED");
    }

    private static ProblemDetail setProblemDetailProperties(ProblemDetail detail, String title) {
        detail.setTitle(title);
        detail.setProperty("timestamp", LocalDateTime.now());

        return detail;
    }
}
