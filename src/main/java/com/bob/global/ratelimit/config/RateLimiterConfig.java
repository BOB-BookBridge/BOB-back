package com.bob.global.ratelimit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.bob.global.ratelimit.config.props.RateLimiterProperties;
import com.bob.global.ratelimit.repository.RateLimitRepository;
import com.bob.global.ratelimit.repository.impl.DistributedRateLimitRepository;
import com.bob.global.ratelimit.repository.impl.MemoryRateLimitRepository;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(RateLimiterProperties.class)
public class  RateLimiterConfig {

    @Bean
    @ConditionalOnProperty(name = "rate-limiter.distributed", havingValue = "false", matchIfMissing = true)
    public RateLimitRepository singleRateLimitRepository() {
        return new MemoryRateLimitRepository();
    }

    @Bean
    @ConditionalOnProperty(name = "rate-limiter.distributed", havingValue = "true")
    public RateLimitRepository distributedRateLimitRepository(StringRedisTemplate stringRedisTemplate) {
        return new DistributedRateLimitRepository(stringRedisTemplate);
    }
}
