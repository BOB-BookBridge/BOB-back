package com.bob.shared.web.annotation.impl;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.shared.web.annotation.AtLeastOneNotNull;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {

    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotNull annotation) {
        this.fields = annotation.anyOf();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null || !value.getClass().isRecord())
            return true;

        RecordComponent[] comps = value.getClass().getRecordComponents();
        boolean valid = Arrays.stream(fields).anyMatch(f -> isPresent(getRecordField(value, comps, f)));
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("변경 사항이 없습니다.").addConstraintViolation();
        }
        return valid;
    }

    private static Object getRecordField(Object record, RecordComponent[] components, String name) {
        for (RecordComponent component : components) {
            if (component.getName().equals(name)) {
                try {
                    return component.getAccessor().invoke(record);
                } catch (Throwable ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private static boolean isPresent(Object value) {
        if (value == null)
            return false;

        if (value instanceof CharSequence sequence)
            return !sequence.toString().trim().isEmpty();

        return true;
    }
}
