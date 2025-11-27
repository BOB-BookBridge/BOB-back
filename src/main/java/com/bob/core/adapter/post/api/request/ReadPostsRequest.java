package com.bob.core.adapter.post.api.request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bob.core.domain.post.repository.dsl.query.ReadPostsQuery;
import com.bob.core.domain.post.repository.dsl.query.SearchKey;
import com.bob.core.domain.post.repository.dsl.query.SearchPrice;
import com.bob.core.domain.post.repository.dsl.query.SortKey;

public record ReadPostsRequest(
    String key,
    String keyword,
    UUID memberId,
    Integer emdId,
    Integer categoryId,
    Integer price,
    String postStatus,
    String bookStatus,
    String sort
) {

    public ReadPostsQuery toQuery(UUID authenticatorId) {
        return ReadPostsQuery.builder()
            .authenticatorId(authenticatorId)
            .key(SearchKey.from(key).orElse(SearchKey.ALL))
            .keyword(keyword)
            .memberId(memberId)
            .emdId(emdId)
            .categoryIds(categoryId != null ? new ArrayList<>(List.of(categoryId)) : null)
            .bookIds(keyword == null || keyword.isBlank() ? null : new ArrayList<>())
            .price(SearchPrice.fromIndex(price).orElse(null))
            .postStatus(postStatus)
            .bookStatus(bookStatus)
            .sortKey(SortKey.from(sort).orElse(SortKey.RECENT))
            .build();
    }
}
