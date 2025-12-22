package com.bob.core.trade.adapter.api.request.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.core.trade.adapter.api.request.validator.impl.TradeKeyParamValidator;

@Target({FIELD, PARAMETER, RECORD_COMPONENT})
@Retention(RUNTIME)
@Constraint(validatedBy = TradeKeyParamValidator.class)
public @interface ValidTradeKey {

    String message() default "검색 조건은 null 혹은 [ALL, SENT, RECEIVED] 중 하나여야 하며 공백을 포함할 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean ignoreCase() default true;

    boolean allowBlank() default false;

    boolean allowNull() default true;
}
