package com.bob.core.domain.post.repository.dsl.query;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchKey {
    ALL("통합"),
    TITLE("제목"),
    AUTHOR("저자");

    private final String label;

    public static Optional<SearchKey> from(String input) {
        if (input == null) {
            return Optional.of(ALL);
        }

        return Arrays.stream(values())
            .filter(key -> key.label.equals(input))
            .findAny();
    }
}
