package com.bob.global.ratelimit.helper;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import com.bob.global.ratelimit.annotation.RateLimit;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitKeyGenerator {

    public static String generateKey(HttpServletRequest request, JoinPoint joinPoint, RateLimit limitInfo) {
        String target = getIdentifier(request, joinPoint, limitInfo.target(), limitInfo.value());
        String apiName = limitInfo.name().isEmpty() ? request.getRequestURI() : limitInfo.name();

        return apiName + ":" + target;
    }

    public static String generateGlobalKey(HttpServletRequest request) {
        String ipAddress = getClientIp(request);

        return "global:ratelimit:" + ipAddress;
    }

    private static String getIdentifier(HttpServletRequest request, JoinPoint joinPoint,
        RateLimit.LimitTarget target, String value
    ) {
        return switch (target) {
            case MEMBER_ID -> getMemberId(value, request, joinPoint);
            case IP -> getClientIp(request);
        };
    }

    private static String getMemberId(String value, HttpServletRequest request, JoinPoint joinPoint) {
        if (!RateLimitExpressionResolver.hasExpression(value)) {
            log.error("MEMBER_ID target requires value parameter. API: {}", request.getRequestURI());

            throw new IllegalArgumentException(
                "MEMBER_ID target requires explicit value parameter in @RateLimit annotation."
            );
        }

        String memberId = RateLimitExpressionResolver.resolveExpression(value, joinPoint, request);

        if (memberId == null || memberId.isEmpty()) {
            log.error("Failed to extract memberId from expression: {} for API: {}", value, request.getRequestURI());

            throw new IllegalArgumentException(
                "Failed to extract memberId from expression: " + value + ". "
                    + "Please check the expression syntax and ensure the parameter/attribute exists."
            );
        }

        log.debug("Extracted memberId from expression: {} -> {}", value, memberId);

        return "member:" + memberId;
    }

    private static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("Proxy-Client-IP");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();

        if (ip != null && ip.contains(","))
            ip = ip.split(",")[0];

        return "ip:" + ip;
    }
}
