package com.bob.web.post.response;

import com.bob.domain.post.service.dto.response.PostSummary;
import com.bob.domain.post.service.dto.response.PostsResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record PostsResponse(
    Long totalCount,
    List<PostResponse> posts
) {

  public static PostsResponse from(PostsResult result, Map<Long, String> statusMap) {
    List<PostResponse> list = result.posts().stream()
        .map(p -> {
          String resolved = "OWNER".equals(p.participation()) ? "OWNER" : statusMap.getOrDefault(p.postId(), "NONE");
          return PostResponse.from(p, resolved);
        }).toList();

    return new PostsResponse(result.totalCount(), list);
  }

  public record PostResponse(
      Long postId,
      Integer categoryId,
      String postTitle,
      String postStatus,
      String thumbnailUrl,
      String bookStatus,
      Integer sellPrice,
      LocalDateTime createdAt,
      String participation
  ) {

    public static PostResponse from(PostSummary summary, String participation) {
      return new PostResponse(
          summary.postId(),
          summary.categoryId(),
          summary.postTitle(),
          summary.postStatus(),
          summary.thumbnailUrl(),
          summary.bookStatus(),
          summary.sellPrice(),
          summary.createdAt(),
          participation
      );
    }
  }
}
