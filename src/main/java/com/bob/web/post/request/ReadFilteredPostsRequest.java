package com.bob.web.post.request;

import com.bob.domain.post.service.dto.query.ReadFilteredPostsQuery;
import com.bob.domain.post.service.dto.query.condition.SearchKey;
import com.bob.domain.post.service.dto.query.condition.SearchPrice;
import com.bob.domain.post.service.dto.query.condition.SortKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ReadFilteredPostsRequest(
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

  public ReadFilteredPostsQuery toQuery() {
    return ReadFilteredPostsQuery.builder()
        .key(SearchKey.from(key).orElse(SearchKey.ALL))
        .keyword(keyword)
        .memberId(memberId)
        .emdId(emdId)
        .categoryIds(categoryId != null ? new ArrayList<>(List.of(categoryId)) : null)
        .price(SearchPrice.fromIndex(price).orElse(null))
        .postStatus(postStatus)
        .bookStatus(bookStatus)
        .sortKey(SortKey.from(sort).orElse(SortKey.RECENT))
        .build();
  }
}