package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import java.util.List;
import java.util.UUID;

public record PostsResult(
    Long totalCount,
    List<PostSummary> posts
) {

  public static PostsResult of(Long totalCount, List<Post> postList, UUID memberId) {
    List<PostSummary> summaries = postList.stream()
        .map(post -> PostSummary.from(post, memberId))
        .toList();

    return new PostsResult(totalCount, summaries);
  }
}
