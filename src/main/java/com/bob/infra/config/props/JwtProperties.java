package com.bob.infra.config.props;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String accessName,
    String refreshName,
    @Min(1) int accessTokenExpireTime,
    @Min(1) int refreshTokenExpireTime
) {

}
