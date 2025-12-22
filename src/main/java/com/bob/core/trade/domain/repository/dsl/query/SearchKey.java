package com.bob.core.trade.domain.repository.dsl.query;

public enum SearchKey {
    ALL, SENT, RECEIVED;

    public static SearchKey convertFrom(String value) {
        if (value == null)
            return ALL;

        return SearchKey.valueOf(value.toUpperCase());
    }
}
