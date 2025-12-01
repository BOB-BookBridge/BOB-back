package com.bob.global.ratelimit.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("처리 제한기 파라미터 표현식 검증 테스트")
class RateLimitExpressionResolverTest {

    private HttpServletRequest mockRequest;
    private JoinPoint mockJoinPoint;
    private MethodSignature mockSignature;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
        mockJoinPoint = mock(JoinPoint.class);
        mockSignature = mock(MethodSignature.class);
        given(mockJoinPoint.getSignature()).willReturn(mockSignature);
    }

    @Test
    void 표현식_정상_해석() {
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {"user123"});

        String result = RateLimitExpressionResolver.resolveExpression("#memberId", mockJoinPoint, mockRequest);

        assertThat(result).isEqualTo("user123");
    }

    @Test
    void 표현식이_null이면_null_반환() {
        String result = RateLimitExpressionResolver.resolveExpression(null, mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 표현식이_빈_문자열이면_null_반환() {
        String result = RateLimitExpressionResolver.resolveExpression("", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 표현식이_공백이면_null_반환() {
        String result = RateLimitExpressionResolver.resolveExpression("   ", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 표현식_해석_실패_시_null_반환() {
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {"user123"});

        String result = RateLimitExpressionResolver.resolveExpression("#invalidParam", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 표현식_평가_결과_null() {
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {null});

        String result = RateLimitExpressionResolver.resolveExpression("#memberId", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 파라미터_배열_길이_불일치_시_표현식_해석_실패() {
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId", "extra"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {"user123"});

        String result = RateLimitExpressionResolver.resolveExpression("#memberId", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 파라미터_이름이_null이면_표현식_해석_실패() {
        given(mockSignature.getParameterNames()).willReturn(null);
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {"user123"});

        String result = RateLimitExpressionResolver.resolveExpression("#memberId", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 파라미터_값이_null이면_표현식_해석_실패() {
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId"});
        given(mockJoinPoint.getArgs()).willReturn(null);

        String result = RateLimitExpressionResolver.resolveExpression("#memberId", mockJoinPoint, mockRequest);

        assertThat(result).isNull();
    }

    @Test
    void 표현식_존재_여부_확인() {
        assertThat(RateLimitExpressionResolver.hasExpression("#memberId")).isTrue();
        assertThat(RateLimitExpressionResolver.hasExpression("value")).isTrue();
    }

    @Test
    void 표현식이_null이면_false_반환() {
        assertThat(RateLimitExpressionResolver.hasExpression(null)).isFalse();
    }

    @Test
    void 표현식이_빈_문자열이면_false_반환() {
        assertThat(RateLimitExpressionResolver.hasExpression("")).isFalse();
    }

    @Test
    void 표현식이_공백이면_false_반환() {
        assertThat(RateLimitExpressionResolver.hasExpression("   ")).isFalse();
    }
}
