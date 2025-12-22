package com.bob.core.post.domain.repository.dsl.query;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ReadPostsQuery(
    UUID authenticatorId, SearchKey key, String keyword, UUID memberId, Integer emdId, List<Integer> categoryIds,
    List<Long> bookIds, SearchPrice price, String postStatus, String bookStatus, SortKey sortKey
) {

    public void updateCategoryIds(List<Integer> categoryIds) {
        this.categoryIds.addAll(categoryIds);
    }

    public void updateBookIds(List<Long> bookIds) {
        this.bookIds.addAll(bookIds);
    }
}
