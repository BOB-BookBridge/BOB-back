package com.bob.core.adapter.chat.api.request.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.core.adapter.chat.api.request.validator.impl.NotBlankIfNotNullValidator;

@Constraint(validatedBy = NotBlankIfNotNullValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankIfNotNull {

    String message() default "공백만으로 이뤄진 문자열은 허용되지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
