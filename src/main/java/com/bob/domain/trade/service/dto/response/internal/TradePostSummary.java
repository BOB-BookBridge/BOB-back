package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradePostSummary(
    Long id,
    String status,
    UUID sellerId,
    Long sellerBookId,
    String title,
    String thumbnailUrl,
    boolean wishOnly
) {

  public static TradePostSummary from(PostDetailResponse post) {
    return TradePostSummary.builder()
        .id(post.postId())
        .status(post.status())
        .sellerId(post.sellerId())
        .sellerBookId(post.sellerBookId())
        .title(post.book().title())
        .thumbnailUrl(post.thumbnailUrl())
        .wishOnly(post.wishOnly())
        .build();
  }
}
