package com.bob.core.application.bookcase.dto.query;

public enum SearchKey {
    ALL, AVAILABLE, UNAVAILABLE;

    public static SearchKey of(String key) {
        if (key == null || key.isBlank())
            return ALL;

        return valueOf(key.trim().toUpperCase());
    }
}
