package com.bob.global.event.sse.manager.dto;

import java.time.LocalDateTime;

public record ChatMessageEmitEvent(
    String messageType,
    String message,
    LocalDateTime sentAt
) {

  public static ChatMessageEmitEvent of(String messageType, String message, LocalDateTime sentAt) {
    return new ChatMessageEmitEvent(messageType, message, sentAt);
  }
}
