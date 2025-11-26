package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.TYPE_MISMATCH;
import static com.bob.global.exception.response.ApplicationError.VALIDATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.global.exception.response.ErrorResponse;

@DisplayName("애플리케이션 예외 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void 사용자_예외_전역_처리() {
        ApplicationException ex = new ApplicationException(ApplicationError.ALREADY_EXISTS_EMAIL);

        ResponseEntity<ErrorResponse> response = handler.handleApplicationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(ApplicationError.ALREADY_EXISTS_EMAIL.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ex.getError().getCode());
        assertThat(response.getBody().message()).isEqualTo(ex.getError().getMessage());
    }

    @Test
    void 유효성_예외_처리() {
        // [요청 예시] POST /members
        // [문제 상황] email 필드가 null 또는 공백으로 전달됨 → @NotBlank 조건 위반
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        given(bindingResult.getFieldErrors()).willReturn(
            Collections.singletonList(new FieldError("object", "email", "이메일은 필수입니다."))
        );

        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        given(ex.getBindingResult()).willReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(VALIDATION_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(VALIDATION_ERROR.getCode());
    }

    @Test
    void 타입_불일치_예외_처리() {
        // [요청 예시] GET /members/{memberId} → /members/abc
        // [문제 상황] UUID 타입인 memberId 파라미터에 문자열 "abc"가 들어와 타입 변환 실패
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "abc", String.class, "memberId", null, null
        );

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(TYPE_MISMATCH.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(TYPE_MISMATCH.getCode());
        assertThat(response.getBody().message()).contains("memberId");
    }
}
