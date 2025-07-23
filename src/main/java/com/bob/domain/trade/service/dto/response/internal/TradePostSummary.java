package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradePostSummary(
    Long postId,
    UUID sellerId,
    String title
) {

  public static TradePostSummary from(PostDetailResponse post) {
    return TradePostSummary.builder()
        .postId(post.postId())
        .sellerId(post.sellerId())
        .title(post.book().title())
        .build();
  }
}
