package com.bob.shared.web.annotation.impl;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.shared.web.annotation.AfterDate;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {

    private LocalDate minDate;
    private String minDateStr;

    @Override
    public void initialize(AfterDate annotation) {
        this.minDateStr = annotation.value();
        this.minDate = LocalDate.parse(minDateStr);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        if (!value.isBefore(minDate))
            return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
            context
                .getDefaultConstraintMessageTemplate()
                .replace("{value}", minDateStr)
        ).addConstraintViolation();

        return false;
    }
}
