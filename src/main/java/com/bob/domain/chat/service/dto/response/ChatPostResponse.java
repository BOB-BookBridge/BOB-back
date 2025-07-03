package com.bob.domain.chat.service.dto.response;

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChatPostResponse(
    Long postId,
    UUID sellerId,
    String title,
    String thumbnailUrl,
    int sellPrice
) {

  public static ChatPostResponse from(PostDetailResponse response) {
    return ChatPostResponse.builder()
        .postId(response.postId())
        .sellerId(response.sellerId())
        .title(response.book().title())
        .thumbnailUrl(response.thumbnailUrl())
        .sellPrice(response.sellPrice())
        .build();
  }
}
