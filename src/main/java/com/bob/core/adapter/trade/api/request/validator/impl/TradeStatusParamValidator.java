package com.bob.core.adapter.trade.api.request.validator.impl;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.bob.core.adapter.trade.api.request.validator.ValidTradeStatus;

public class TradeStatusParamValidator implements ConstraintValidator<ValidTradeStatus, String> {

    private static final Set<String> ALLOWED = Set.of(
        "ALL", "CANCELED", "REQUESTED", "ACCEPTED", "RESERVED", "COMPLETED", "REJECTED"
    );
    private boolean ignoreCase;
    private boolean allowBlank;
    private boolean allowNull;

    @Override
    public void initialize(ValidTradeStatus ann) {
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

    private static boolean hasWhitespace(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i)))
                return true;
        }
        return false;
    }
}
