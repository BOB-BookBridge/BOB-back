package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import java.util.List;

public record PostsResponse(
    Long totalCount,
    List<PostSummary> posts
) {

  public static PostsResponse of(Long totalCount, List<Post> postList) {
    List<PostSummary> summaries = postList.stream()
        .map(PostSummary::from)
        .toList();

    return new PostsResponse(totalCount, summaries);
  }
}
