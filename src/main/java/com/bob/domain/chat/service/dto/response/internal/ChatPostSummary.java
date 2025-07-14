package com.bob.domain.chat.service.dto.response.internal;

import java.util.UUID;

public record ChatPostSummary(
    Long id,
    String status,
    UUID sellerId,
    String title,
    String thumbnailUrl,
    int sellPrice
) {

  public static ChatPostSummary of(
      Long id, String status, UUID sellerId, String title, String thumbnailUrl, int sellPrice
  ) {
    return new ChatPostSummary(id, status, sellerId, title, thumbnailUrl, sellPrice);
  }
}
