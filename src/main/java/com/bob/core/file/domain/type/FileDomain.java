package com.bob.core.file.domain.type;

public enum FileDomain {
    POST, CHAT;

    public static FileDomain of(String value) {
        return FileDomain.valueOf(value.toUpperCase());
    }
}
