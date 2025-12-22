package com.bob.core.trade.adapter.api.request.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.core.trade.adapter.api.request.validator.impl.TradeStatusParamValidator;

@Target({TYPE_USE, FIELD, PARAMETER, RECORD_COMPONENT})
@Retention(RUNTIME)
@Constraint(validatedBy = TradeStatusParamValidator.class)
public @interface ValidTradeStatus {

    String message() default "각 상태 조건은 [ALL, CANCELED, REQUESTED, ACCEPTED, RESERVED, COMPLETED, REJECTED] 중 "
        + "하나여야 하며 공백을 포함할 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean ignoreCase() default true;

    boolean allowBlank() default false;

    boolean allowNull() default false;
}
