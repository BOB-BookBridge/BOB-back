package com.bob.domain.chat.service.dto.response.internal;

public record ChatTradeSummary(
    Long id
) {

  public static ChatTradeSummary of(Long id) {
    return new ChatTradeSummary(id);
  }
}
