package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradePostSummary(
    Long id,
    UUID sellerId,
    String title,
    String thumbnailUrl
) {

  public static TradePostSummary from(PostDetailResponse post) {
    return TradePostSummary.builder()
        .id(post.postId())
        .sellerId(post.sellerId())
        .title(post.book().title())
        .thumbnailUrl(post.thumbnailUrl())
        .build();
  }
}
