package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.PostFavorite;
import java.util.List;
import lombok.Builder;

@Builder
public record PostFavoritesResponse(
    Long totalCount,
    List<PostSummary> postFavorites
) {

  public static PostFavoritesResponse of(Long totalCount, List<PostFavorite> postList) {
    List<PostSummary> summaries = postList.stream()
        .map((postFavorite) -> PostSummary.from(postFavorite.getPost()))
        .toList();

    return new PostFavoritesResponse(totalCount, summaries);
  }
}
