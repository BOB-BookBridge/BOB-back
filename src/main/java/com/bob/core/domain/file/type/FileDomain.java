package com.bob.core.domain.file.type;

public enum FileDomain {
    POST, CHAT;

    public static FileDomain of(String value) {
        return FileDomain.valueOf(value.toUpperCase());
    }
}
