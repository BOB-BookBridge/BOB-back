package com.bob.global.ratelimit.config.props;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    /**
     * Rate Limiter 활성화 여부
     * <p>
     * false: 모든 API Rate Limit 비활성화. ({@literal @}RateLimit이 명시된 API 포함)
     * </p>
     */
    private boolean enabled = true;

    /**
     * 분산 환경에서 Redis를 통한 처리 제한 사용 여부
     * <p>
     * true: Redis 기반 분산 처리 제한 (다중 서버 환경, 요청 횟수 공유)
     * <br>
     * false: 로컬 메모리 기반 처리 제한 (단일 서버 환경, 서버별 독립적 카운트)
     * </p>
     * 기본값: false
     */
    private boolean distributed = false;

    /**
     * 모든 RestController 메서드에 기본 처리 제한 적용 여부
     * <p>
     * true: {@literal @}RateLimit, {@literal @}DisableRateLimit 이 없는 모든 API에 전역 제한 적용
     * <br>
     * false: {@literal @}RateLimit이 명시된 API에만 제한 적용
     * </p>
     * 기본값: true
     */
    private boolean global = true;
}
