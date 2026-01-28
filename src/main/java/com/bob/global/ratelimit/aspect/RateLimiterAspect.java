package com.bob.global.ratelimit.aspect;

import static com.bob.global.ratelimit.helper.RateLimitKeyGenerator.generateGlobalKey;
import static com.bob.global.ratelimit.helper.RateLimitKeyGenerator.generateKey;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bob.global.ratelimit.annotation.DisableRateLimit;
import com.bob.global.ratelimit.annotation.RateLimit;
import com.bob.global.ratelimit.config.props.RateLimiterProperties;
import com.bob.global.ratelimit.exception.RateLimitExceededException;
import com.bob.global.ratelimit.repository.RateLimitRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private static final long GLOBAL_WINDOW_SECOND = 5L;
    private static final int GLOBAL_MAX_REQUEST = 30;

    private final RateLimitRepository repository;
    private final RateLimiterProperties properties;

    @Before("@annotation(rateLimit)")
    public void enforceRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        if (!properties.isEnabled())
            return;

        HttpServletRequest request = getHttpServletRequest();
        String key = generateKey(request, joinPoint, rateLimit);

        boolean allowed = repository.isAllowed(key, rateLimit.windowSecond(), rateLimit.maxRequest());

        if (!allowed) {
            long waitSeconds = repository.getWaitForRefill(key, rateLimit.windowSecond(), rateLimit.maxRequest());

            throw new RateLimitExceededException(waitSeconds);
        }
    }

    @Before("@within(org.springframework.web.bind.annotation.RestController) && execution(public * *(..))")
    public void enforceGlobalRateLimit(JoinPoint joinPoint) {
        if (!properties.isEnabled() || !properties.isGlobal())
            return;

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.isAnnotationPresent(DisableRateLimit.class) ||
            method.isAnnotationPresent(RateLimit.class)
        )
            return;

        HttpServletRequest request = getHttpServletRequest();
        String key = generateGlobalKey(request);

        boolean allowed = repository.isAllowed(key, GLOBAL_WINDOW_SECOND, GLOBAL_MAX_REQUEST);

        if (!allowed) {
            long waitSeconds = repository.getWaitForRefill(key, GLOBAL_WINDOW_SECOND, GLOBAL_MAX_REQUEST);
            throw new RateLimitExceededException(waitSeconds);
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }
}
