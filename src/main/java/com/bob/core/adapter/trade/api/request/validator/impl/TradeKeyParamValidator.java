package com.bob.core.adapter.trade.api.request.validator.impl;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.core.adapter.trade.api.request.validator.ValidTradeKey;

public class TradeKeyParamValidator implements ConstraintValidator<ValidTradeKey, String> {

    private static final Set<String> ALLOWED = Set.of("ALL", "SENT", "RECEIVED");
    private boolean ignoreCase;
    private boolean allowBlank;
    private boolean allowNull;

    private static boolean hasWhitespace(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i)))
                return true;
        }
        return false;
    }

    @Override
    public void initialize(ValidTradeKey ann) {
        ignoreCase = ann.ignoreCase();
        allowBlank = ann.allowBlank();
        allowNull = ann.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null)
            return allowNull;
        if (hasWhitespace(value))
            return false;
        if (value.isEmpty())
            return allowBlank;
        return ALLOWED.contains(ignoreCase ? value.toUpperCase() : value);
    }
}
