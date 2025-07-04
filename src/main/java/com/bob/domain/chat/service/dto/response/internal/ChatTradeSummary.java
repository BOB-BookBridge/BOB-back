package com.bob.domain.chat.service.dto.response.internal;

public record ChatTradeSummary(
    Long id,
    String status
) {

  public static ChatTradeSummary of(Long id, String status) {
    return new ChatTradeSummary(id, status);
  }
}
