package com.bob.global.ratelimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String name() default "";

    long windowSecond();

    int maxRequest();

    LimitTarget target() default LimitTarget.IP;

    String value() default "";

    enum LimitTarget {
        IP, MEMBER_ID
    }
}
