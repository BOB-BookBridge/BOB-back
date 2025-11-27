package com.bob.core.domain.trade.repository.dsl.query;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.domain.trade.status.Status;

@Builder
public record ReadTradesQuery(UUID memberId, SearchKey key, List<Status> statuses) {

    public static ReadTradesQuery of(UUID memberId, String key, List<String> statuses) {
        return ReadTradesQuery.builder()
            .memberId(memberId)
            .key(SearchKey.convertFrom(key))
            .statuses(Status.convertFrom(statuses))
            .build();
    }
}
