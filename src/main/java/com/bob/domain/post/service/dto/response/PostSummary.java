package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostSummary(
    Long postId,
    Integer categoryId,
    String postTitle,
    String postStatus,
    String thumbnailUrl,
    String bookStatus,
    Integer sellPrice,
    LocalDateTime createdAt
) {

  public static PostSummary from(Post post) {
    return PostSummary.builder()
        .postId(post.getId())
        .categoryId(post.getCategory().getId())
        .postTitle(post.getBook().getTitle())
        .postStatus(post.getTradeProgress().name())
        .thumbnailUrl(post.getThumbnailUrl())
        .bookStatus(post.getBookStatus().name())
        .sellPrice(post.getSellPrice())
        .createdAt(post.getCreatedAt())
        .build();
  }
}
