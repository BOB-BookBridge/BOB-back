package com.bob.shared.web.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.shared.web.annotation.impl.AtLeastOneNotNullValidator;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
public @interface AtLeastOneNotNull {

    String[] anyOf();

    String message() default "변경 사항이 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
