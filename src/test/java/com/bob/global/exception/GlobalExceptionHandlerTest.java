package com.bob.global.exception;

import static com.bob.global.exception.response.ApplicationError.MEMBER_EMAIL_DUPLICATED;
import static com.bob.global.exception.response.ApplicationError.SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.ratelimit.exception.RateLimitExceededException;

@DisplayName("애플리케이션 예외 핸들러 테스트")
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletResponse response;

    @Test
    void 비즈니스_예외_처리() {
        // [요청 예시] POST /members with duplicate email
        // [문제 상황] 중복된 이메일로 회원가입 시도
        ApplicationException ex = new ApplicationException(MEMBER_EMAIL_DUPLICATED);

        ProblemDetail problemDetail = handler.handleApplicationException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(MEMBER_EMAIL_DUPLICATED.getStatus().value());
        assertThat(problemDetail.getTitle()).isEqualTo(MEMBER_EMAIL_DUPLICATED.name());
        assertThat(problemDetail.getDetail()).isEqualTo(ex.getError().getMessage());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 제약조건위반_예외_처리() {
        // [요청 예시] GET /members?email=invalid&age=10
        // [문제 상황] @Validated 또는 @Valid로 검증할 때 위반
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        given(violation.getMessage()).willReturn("이메일 형식이 올바르지 않습니다.");

        ConstraintViolationException ex = new ConstraintViolationException("Constraint violation", Set.of(violation));

        ProblemDetail problemDetail = handler.handleConstraintViolation(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("CONSTRAINT_VIOLATION");
        assertThat(problemDetail.getDetail()).isEqualTo("이메일 형식이 올바르지 않습니다.");
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 처리_제한_예외_처리() {
        // [요청 예시] POST /member/signup
        // [문제 상황] 회원 생성 시도 횟수 초과
        RateLimitExceededException ex = new RateLimitExceededException(30L);

        ProblemDetail problemDetail = handler.handleRateLimitExceeded(ex, response);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(problemDetail.getTitle()).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(problemDetail.getDetail()).contains("요청 한도를 초과했습니다.");
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 서버_예외_처리() {
        // [문제 상황] 예상하지 못한 서버 내부 오류 발생
        // ResponseEntityExceptionHandler가 처리하는 검증 예외들은 자동 처리됨
        IllegalArgumentException ex = new IllegalArgumentException("Unexpected error");

        ProblemDetail problemDetail = handler.handleServerException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(SERVER_ERROR.getStatus().value());
        assertThat(problemDetail.getTitle()).isEqualTo(SERVER_ERROR.name());
        assertThat(problemDetail.getDetail()).isEqualTo(SERVER_ERROR.getMessage());
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 요청값_검증_예외_처리() {
        // [요청 예시] POST /reports/posts/1 with {"reportedId": null, "reason": "spam"}
        // [문제 상황] @Valid @RequestBody로 검증할 때 필드 값이 유효하지 않음
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("registerReportRequest", "reportedId", "신고자 ID는 필수입니다");
        given(bindingResult.getFieldErrors()).willReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail problemDetail = handler.handleValidationException(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("VALIDATION_ERROR");
        assertThat(problemDetail.getDetail()).isEqualTo("신고자 ID는 필수입니다");
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }

    @Test
    void 타입_불일치_예외_처리() {
        // [요청 예시] GET /posts/invalid-uuid
        // [문제 상황] PathVariable이나 RequestParam의 타입이 맞지 않음
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class);
        given(ex.getName()).willReturn("postId");
        given(ex.getValue()).willReturn("invalid-id");

        ProblemDetail problemDetail = handler.handleTypeMismatch(ex);

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("TYPE_MISMATCH");
        assertThat(problemDetail.getDetail()).isEqualTo("[postId]의 값 'invalid-id'은(는) 올바른 형식이 아닙니다.");
        assertThat(problemDetail.getProperties()).containsKeys("timestamp");
    }
}
