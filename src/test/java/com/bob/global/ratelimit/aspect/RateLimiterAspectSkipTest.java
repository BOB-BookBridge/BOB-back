package com.bob.global.ratelimit.aspect;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bob.global.ratelimit.aspect.stub.TestController;
import com.bob.global.ratelimit.config.props.RateLimiterProperties;
import com.bob.global.ratelimit.repository.RateLimitRepository;

@DisplayName("처리 제한기 무시 테스트")
@ExtendWith(MockitoExtension.class)
class RateLimiterAspectSkipTest {

    @InjectMocks
    private RateLimiterAspect rateLimiterAspect;

    @Mock
    private RateLimitRepository rateLimitRepository;

    @Mock
    private RateLimiterProperties rateLimiterProperties;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void 전역_제한_비활성화_시_무시() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        given(rateLimiterProperties.isGlobal()).willReturn(false);

        rateLimiterAspect.enforceGlobalRateLimit(joinPoint);

        then(rateLimitRepository).should(never()).isAllowed(anyString(), anyLong(), anyInt());
    }

    @Test
    void 전역_제한_DisableRateLimit_어노테이션_있으면_무시() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Method method = TestController.class.getMethod("noRateLimitEndpoint");
        given(rateLimiterProperties.isGlobal()).willReturn(true);
        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(methodSignature.getMethod()).willReturn(method);

        rateLimiterAspect.enforceGlobalRateLimit(joinPoint);

        then(rateLimitRepository).should(never()).isAllowed(anyString(), anyLong(), anyInt());
    }

    @Test
    void 전역_제한_RateLimit_어노테이션_있으면_무시() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Method method = TestController.class.getMethod("rateLimitedEndpoint");
        given(rateLimiterProperties.isGlobal()).willReturn(true);
        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(methodSignature.getMethod()).willReturn(method);

        rateLimiterAspect.enforceGlobalRateLimit(joinPoint);

        then(rateLimitRepository).should(never()).isAllowed(anyString(), anyLong(), anyInt());
    }
}
