package com.bob.support.fixture.global;

import java.lang.annotation.Annotation;

import com.bob.global.ratelimit.annotation.RateLimit;

public class RateLimitFixture {

    public static RateLimit createRateLimit(RateLimit.LimitTarget target, String value) {
        return createRateLimit(target, value, "");
    }

    public static RateLimit createRateLimit(RateLimit.LimitTarget target, String value, String name) {
        return new RateLimit() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RateLimit.class;
            }

            @Override
            public LimitTarget target() {
                return target;
            }

            @Override
            public String value() {
                return value;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int maxRequest() {
                return 10;
            }

            @Override
            public long windowSecond() {
                return 60;
            }
        };
    }
}
