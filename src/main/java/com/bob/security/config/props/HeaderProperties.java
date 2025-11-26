package com.bob.security.config.props;

import jakarta.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record HeaderProperties(
    String accessName,
    String refreshName,
    @Min(1) Long accessTokenExpireTime,
    @Min(1) Long refreshTokenExpireTime
) {

}
