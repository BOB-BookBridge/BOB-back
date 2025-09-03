package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.TYPE_MISMATCH;
import static com.bob.global.exception.response.ApplicationError.VALIDATION_ERROR;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.exception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
    ApplicationError error = ex.getError();
    ErrorResponse response = new ErrorResponse(error.getCode(), error.getMessage());
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
        .body(new ErrorResponse(VALIDATION_ERROR.getCode(), detailMessage));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String message = String.format("[%s]의 값 '%s'은(는) 올바른 형식이 아닙니다.", ex.getName(), ex.getValue());
    return ResponseEntity
        .status(TYPE_MISMATCH.getStatus())
        .body(new ErrorResponse(TYPE_MISMATCH.getCode(), message));
  }

  // TODO : Parameter 기반 요청 시 ConstraintViolationException.class 추가
}
