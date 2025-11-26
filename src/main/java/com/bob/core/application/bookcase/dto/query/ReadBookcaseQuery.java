package com.bob.core.application.bookcase.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadBookcaseQuery(UUID memberId, SearchKey key, List<Long> requires) {

    public static ReadBookcaseQuery of(UUID memberId) {
        return new ReadBookcaseQuery(memberId, SearchKey.ALL, List.of());
    }

    public static ReadBookcaseQuery of(UUID memberId, String key, List<Long> requires) {
        return new ReadBookcaseQuery(memberId, SearchKey.of(key), requires);
    }
}
