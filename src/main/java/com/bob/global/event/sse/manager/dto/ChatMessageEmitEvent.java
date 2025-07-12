package com.bob.global.event.sse.manager.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageEmitEvent(
    String messageType,
    String message,
    List<String> fileNames,
    LocalDateTime sentAt
) {

  public static ChatMessageEmitEvent of(String message, List<String> fileNames, LocalDateTime sentAt) {
    return new ChatMessageEmitEvent(resolveMessageType(message, fileNames), message, fileNames, sentAt);
  }

  private static String resolveMessageType(String message, List<String> fileNames) {
    boolean hasMessage = message != null && !message.isBlank();
    boolean hasImage = fileNames != null && !fileNames.isEmpty();

    if (hasMessage && hasImage) {
      return "MIX";
    }
    if (hasMessage) {
      return "TEXT";
    }
    return "IMAGE";
  }
}
