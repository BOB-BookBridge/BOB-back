package com.bob.global.ratelimit.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super("요청 한도를 초과했습니다. " + retryAfterSeconds + "초 후 다시 시도해주세요.");
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
