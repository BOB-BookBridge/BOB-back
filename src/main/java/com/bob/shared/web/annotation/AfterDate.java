package com.bob.shared.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.shared.web.annotation.impl.AfterDateValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterDateValidator.class)
public @interface AfterDate {

    String value(); // "yyyy-MM-dd"

    String message() default "{value} 이후 날짜만 허용됩니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
