package com.bob.domain.post.service.dto.query;

import com.bob.domain.post.service.dto.query.condition.SearchKey;
import com.bob.domain.post.service.dto.query.condition.SearchPrice;
import com.bob.domain.post.service.dto.query.condition.SortKey;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReadFilteredPostsQuery(
    UUID authenticatorId,
    SearchKey key,
    String keyword,
    UUID memberId,
    Integer emdId,
    List<Integer> categoryIds,
    List<Long> bookIds,
    SearchPrice price,
    String postStatus,
    String bookStatus,
    SortKey sortKey
) {

  public void updateCategoryIds(List<Integer> categoryIds) {
    this.categoryIds.addAll(categoryIds);
  }

  public void updateBookIds(List<Long> bookIds) {
    this.bookIds.addAll(bookIds);
  }
}
