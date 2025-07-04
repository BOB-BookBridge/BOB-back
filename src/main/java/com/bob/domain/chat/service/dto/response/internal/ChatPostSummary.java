package com.bob.domain.chat.service.dto.response.internal;

public record ChatPostSummary(
    Long id,
    String title,
    String thumbnailUrl,
    int sellPrice
) {

  public static ChatPostSummary of(Long id, String title, String thumbnailUrl, int sellPrice) {
    return new ChatPostSummary(id, title, thumbnailUrl, sellPrice);
  }
}
