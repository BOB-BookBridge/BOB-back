package com.bob.core.adapter.chat.api.request.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.core.adapter.chat.api.request.validator.NotBlankIfNotNull;

public class NotBlankIfNotNullValidator implements ConstraintValidator<NotBlankIfNotNull, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.trim().isEmpty();
    }
}
