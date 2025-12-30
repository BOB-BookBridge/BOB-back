package com.bob.shared.web.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.shared.web.annotation.impl.AllowedValuesValidator;

@Retention(RUNTIME)
@Target({FIELD, PARAMETER, TYPE_USE})
@Constraint(validatedBy = AllowedValuesValidator.class)
public @interface AllowedValues {

    String message() default "허용되지 않은 값입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();

    boolean ignoreCase() default false;

    boolean allowNull() default true;

    boolean allowBlank() default false;
}

