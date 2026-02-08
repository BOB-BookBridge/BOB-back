package com.bob.core.post.application.dto.result;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.post.domain.Post;

@Builder
public record PostSummary(
    Long id,
    Integer categoryId,
    String title,
    String status,
    String tradeStatus,
    String thumbnailUrl,
    String bookStatus,
    Integer price,
    LocalDateTime createdAt,
    String participation
) {

    public static PostSummary of(Post post, UUID memberId) {
        String participation = Objects.equals(post.getWriterId(), memberId) ? "OWNER" : "NONE";
        return PostSummary.builder()
            .id(post.getId())
            .categoryId(post.getCategoryId())
            .title(post.getTitle())
            .status(post.getStatus().name())
            .tradeStatus(post.getTradeProgress().name())
            .thumbnailUrl(post.getThumbnailUrl())
            .bookStatus(post.getBookStatus().name())
            .price(post.getPrice())
            .createdAt(post.getCreatedAt())
            .participation(participation)
            .build();
    }
}
