package com.bob.web.chat.request.validator.impl;

import com.bob.web.chat.request.validator.NotBlankIfNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfNotNullValidator implements ConstraintValidator<NotBlankIfNotNull, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || !value.trim().isEmpty();
  }
}
