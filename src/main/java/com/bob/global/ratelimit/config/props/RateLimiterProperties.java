package com.bob.global.ratelimit.config.props;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private boolean distributed = false;

    private boolean global = true;
}
