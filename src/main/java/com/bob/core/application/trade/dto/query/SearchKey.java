package com.bob.core.application.trade.dto.query;

public enum SearchKey {
    ALL, SENT, RECEIVED;

    public static SearchKey convertFrom(String value) {
        if (value == null)
            return ALL;

        return SearchKey.valueOf(value.toUpperCase());
    }
}
