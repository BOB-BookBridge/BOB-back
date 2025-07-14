package com.bob.global.event.sse.manager.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageEmitEvent(
    long id,
    String type,
    String content,
    List<String> fileNames,
    LocalDateTime sentAt
) {

  public static ChatMessageEmitEvent of(String id, String content, List<String> fileNames, LocalDateTime sentAt) {
    return new ChatMessageEmitEvent(Long.parseLong(id), resolveMessageType(content, fileNames), content, fileNames, sentAt);
  }

  private static String resolveMessageType(String content, List<String> fileNames) {
    boolean hasMessage = content != null && !content.isBlank();
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
