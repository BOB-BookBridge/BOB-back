package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.SERVER_ERROR;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.ratelimit.exception.RateLimitExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex) {
        log.error("Unexpected exception", ex);
        ProblemDetail problemDetail = forStatusAndDetail(SERVER_ERROR.getStatus(), SERVER_ERROR.getMessage());
        return setProblemDetailProperties(ex, problemDetail, SERVER_ERROR.name());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleServerException(Exception ex) {
        log.warn("Invalid argument or state", ex);
        ProblemDetail problemDetail = forStatusAndDetail(SERVER_ERROR.getStatus(), SERVER_ERROR.getMessage());
        return setProblemDetailProperties(ex, problemDetail, SERVER_ERROR.name());
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String detailMessage = bindingResult.getFieldErrors().stream()
            .map(error -> String.format("%s", error.getDefaultMessage()))
            .findFirst()
            .orElse("유효하지 않은 요청값입니다.");

        ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.BAD_REQUEST, detailMessage);
        return setProblemDetailProperties(problemDetail, "VALIDATION_ERROR");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("[%s]의 값 '%s'은(는) 올바른 형식이 아닙니다.", ex.getName(), ex.getValue());

        ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        return setProblemDetailProperties(problemDetail, "TYPE_MISMATCH");
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

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleAsyncRequestNotUsableException(AsyncRequestNotUsableException ex) {
        log.debug("Async response already unusable: {}", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problemDetail = forStatusAndDetail(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        return setProblemDetailProperties(ex, problemDetail, "ACCESS_DENIED");
    }

    private static ProblemDetail setProblemDetailProperties(ProblemDetail detail, String title) {
        detail.setTitle(title);
        detail.setProperty("timestamp", LocalDateTime.now());

        return detail;
    }

    private static ProblemDetail setProblemDetailProperties(Exception ex, ProblemDetail detail, String title) {
        detail.setTitle(title);
        detail.setProperty("timestamp", LocalDateTime.now());
        detail.setProperty("error-message", ex.getMessage());

        return detail;
    }
}
