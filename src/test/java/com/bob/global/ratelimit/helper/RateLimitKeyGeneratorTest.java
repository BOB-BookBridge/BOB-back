package com.bob.global.ratelimit.helper;

import static com.bob.global.ratelimit.annotation.RateLimit.LimitTarget;
import static com.bob.support.fixture.global.RateLimitFixture.createRateLimit;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.global.ratelimit.annotation.RateLimit;

@DisplayName("처리 제한기 키 생성기 테스트")
class RateLimitKeyGeneratorTest {

    private HttpServletRequest mockRequest;
    private MethodSignature mockSignature;
    private JoinPoint mockJoinPoint;

    @BeforeEach
    void setUp() {
        mockRequest = mock(HttpServletRequest.class);
        mockJoinPoint = mock(JoinPoint.class);
        mockSignature = mock(MethodSignature.class);
        given(mockJoinPoint.getSignature()).willReturn(mockSignature);
    }

    @Test
    void IP_기반_키_생성() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn(null);
        given(mockRequest.getRemoteAddr()).willReturn("192.168.1.100");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:192.168.1.100");
    }

    @Test
    void IP_기반_키_생성_X_Forwarded_For_헤더() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn("203.0.113.5");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:203.0.113.5");
    }

    @Test
    void 회원_ID_기반_키_생성() {
        RateLimit rateLimit = createRateLimit(LimitTarget.MEMBER_ID, "#memberId");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId", "message"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {MEMBER_ID.toString(), "Hello"});

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:member:" + MEMBER_ID);
    }

    @Test
    void 회원_ID_기반_키_생성_커스텀_name() {
        RateLimit rateLimit = createRateLimit(LimitTarget.MEMBER_ID, "#memberId", "dummy:test");
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId", "message"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {MEMBER_ID.toString(), "Test"});

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("dummy:test:member:" + MEMBER_ID);
    }

    @Test
    void 회원_ID_기반_키_생성_시_value가_없으면_예외가_발생한다() {
        RateLimit rateLimit = createRateLimit(LimitTarget.MEMBER_ID, "");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        assertThatThrownBy(() -> RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("MEMBER_ID target requires explicit value parameter");
    }

    @Test
    void 회원_ID_기반_키_생성_시_유효하지_않은_파라미터면_예외가_발생한다() {
        RateLimit rateLimit = createRateLimit(LimitTarget.MEMBER_ID, "#invalidParameter");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");
        given(mockSignature.getParameterNames()).willReturn(new String[] {"memberId"});
        given(mockJoinPoint.getArgs()).willReturn(new Object[] {MEMBER_ID.toString()});

        assertThatThrownBy(() -> RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Failed to extract memberId from expression");
    }

    @Test
    void IP_기반_키_생성_Proxy_Client_IP_헤더() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn(null);
        given(mockRequest.getHeader("Proxy-Client-IP")).willReturn("10.20.30.40");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:10.20.30.40");
    }

    @Test
    void IP_기반_키_생성_X_Forwarded_For가_unknown() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn("unknown");
        given(mockRequest.getHeader("Proxy-Client-IP")).willReturn("10.20.30.40");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:10.20.30.40");
    }

    @Test
    void IP_기반_키_생성_RemoteAddr_사용() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn(null);
        given(mockRequest.getHeader("Proxy-Client-IP")).willReturn(null);
        given(mockRequest.getRemoteAddr()).willReturn("127.0.0.1");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:127.0.0.1");
    }

    @Test
    void IP_기반_키_생성_여러_IP_포함_시_첫번째_IP_사용() {
        RateLimit rateLimit = createRateLimit(LimitTarget.IP, "");
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn("203.0.113.5, 198.51.100.1, 192.0.2.1");
        given(mockRequest.getRequestURI()).willReturn("/dummy/test");

        String key = RateLimitKeyGenerator.generateKey(mockRequest, mockJoinPoint, rateLimit);

        assertThat(key).isEqualTo("/dummy/test:ip:203.0.113.5");
    }

    @Test
    void 전역_키_생성() {
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn("192.168.1.100");

        String key = RateLimitKeyGenerator.generateGlobalKey(mockRequest);

        assertThat(key).isEqualTo("global:ratelimit:ip:192.168.1.100");
    }

    @Test
    void 전역_키_생성_여러_IP_포함() {
        given(mockRequest.getHeader("X-Forwarded-For")).willReturn("10.0.0.1, 10.0.0.2");

        String key = RateLimitKeyGenerator.generateGlobalKey(mockRequest);

        assertThat(key).isEqualTo("global:ratelimit:ip:10.0.0.1");
    }
}
