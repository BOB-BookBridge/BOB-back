package com.bob.global.event.sse.manager.dto;

import static com.bob.global.utils.stream.StreamUtils.forEachWithIndex;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ChatMessageEmitEvent(
    long id,
    String type,
    String content,
    List<ChatImage> images,
    LocalDateTime sentAt
) {

  public static ChatMessageEmitEvent of(boolean isSystem, String id, String content, List<String> fileNames, LocalDateTime sentAt) {
    return new ChatMessageEmitEvent(Long.parseLong(id), resolveMessageType(isSystem, content, fileNames), content, withSequence(fileNames), sentAt);
  }

  private static String resolveMessageType(boolean isSystem, String content, List<String> fileNames) {
    if (isSystem) {
      return "SYSTEM";
    }

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

  private static List<ChatImage> withSequence(List<String> fileNames) {
    if (fileNames == null || fileNames.isEmpty()) {
      return List.of();
    }
    List<ChatImage> result = new ArrayList<>();
    forEachWithIndex(fileNames, (i, name) -> result.add(new ChatImage(i, name)));
    return result;
  }

  public record ChatImage(int sequence, String fileName) {}
}
