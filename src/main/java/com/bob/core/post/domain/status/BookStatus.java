package com.bob.core.post.domain.status;

import java.util.Arrays;

public enum BookStatus {
    BEST, HIGH, MEDIUM, LOW;

    public static BookStatus from(String status) {
        return Arrays.stream(BookStatus.values())
            .filter(value -> value.name().equalsIgnoreCase(status))
            .findFirst()
            .orElse(null);
    }
}
