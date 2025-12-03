package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.TYPE_MISMATCH;
import static com.bob.global.exception.response.ApplicationError.VALIDATION_ERROR;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.exception.response.ErrorResponse;
import com.bob.global.ratelimit.exception.RateLimitExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        ApplicationError error = ex.getError();
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(error.getStatus()).body(response);
    }

    // TODO : Parameter 기반 요청 시 BindException.class 추가
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String detailMessage = bindingResult.getFieldErrors().stream()
            .map(error -> String.format("%s", error.getDefaultMessage()))
            .findFirst()
            .orElse(VALIDATION_ERROR.getMessage());

        return ResponseEntity
            .status(VALIDATION_ERROR.getStatus())
            .body(new ErrorResponse(detailMessage));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("[%s]의 값 '%s'은(는) 올바른 형식이 아닙니다.", ex.getName(), ex.getValue());
        return ResponseEntity
            .status(TYPE_MISMATCH.getStatus())
            .body(new ErrorResponse(message));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex,
        HttpServletResponse response) {
        response.addHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(errorResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleServerException(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // TODO : Parameter 기반 요청 시 BindException.class 추가
    // TODO : Parameter 기반 요청 시 ConstraintViolationException.class 추가
}
