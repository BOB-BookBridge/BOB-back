package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
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
    LocalDateTime createdAt,
    String participation
) {

  public static PostSummary from(Post post) {
    return PostSummary.builder()
        .postId(post.getId())
        .categoryId(post.getCategory().getId())
        .postTitle(post.getTitle())
        .postStatus(post.getTradeProgress().name())
        .thumbnailUrl(post.getThumbnailUrl())
        .bookStatus(post.getBookStatus().name())
        .sellPrice(post.getSellPrice())
        .createdAt(post.getCreatedAt())
        .participation("NONE")
        .build();
  }

  public static PostSummary from(Post post, UUID memberId) {
    String participation = Objects.equals(post.getSellerId(), memberId) ? "OWNER" : "NONE";
    return PostSummary.builder()
        .postId(post.getId())
        .categoryId(post.getCategory().getId())
        .postTitle(post.getTitle())
        .postStatus(post.getTradeProgress().name())
        .thumbnailUrl(post.getThumbnailUrl())
        .bookStatus(post.getBookStatus().name())
        .sellPrice(post.getSellPrice())
        .createdAt(post.getCreatedAt())
        .participation(participation)
        .build();
  }
}
