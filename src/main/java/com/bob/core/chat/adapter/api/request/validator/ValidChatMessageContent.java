package com.bob.core.chat.adapter.api.request.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.bob.core.chat.adapter.api.request.validator.impl.ChatMessageContentValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChatMessageContentValidator.class)
public @interface ValidChatMessageContent {

    String message() default "메시지 또는 사진 중 하나는 반드시 포함되어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
