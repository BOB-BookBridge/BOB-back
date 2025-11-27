package com.bob.core.domain.post.repository.dsl.query;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchPrice {
    UNDER_5000(0, 5_000),
    BETWEEN_5000_AND_10000(5_000, 10_000),
    BETWEEN_10000_AND_20000(10_000, 20_000),
    OVER_20000(20_000, Integer.MAX_VALUE);

    private final int minPrice;
    private final int maxPrice;

    public static Optional<SearchPrice> fromIndex(Integer index) {
        if (index == null || index < 0 || index >= values().length) {
            return Optional.empty();
        }
        return Optional.of(values()[index]);
    }
}
