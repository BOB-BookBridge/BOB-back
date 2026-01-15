package com.bob.shared.web.annotation.impl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.shared.web.annotation.AllowedValues;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private Set<String> allowedValues;
    private boolean ignoreCase;
    private boolean allowNull;

    @Override
    public void initialize(AllowedValues annotation) {
        ignoreCase = annotation.ignoreCase();
        allowNull = annotation.allowNull();

        if (ignoreCase) {
            allowedValues = Stream.of(annotation.value())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        } else {
            allowedValues = Set.of(annotation.value());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty())
            return allowNull;

        if (hasWhitespace(value))
            return false;

        String compareValue = ignoreCase ? value.toUpperCase() : value;
        return allowedValues.contains(compareValue);
    }

    private static boolean hasWhitespace(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i)))
                return true;
        }
        return false;
    }
}

