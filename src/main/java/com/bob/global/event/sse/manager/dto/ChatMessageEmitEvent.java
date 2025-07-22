package com.bob.global.event.sse.manager.dto;

import static com.bob.global.utils.stream.StreamUtils.forEachWithIndex;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ChatMessageEmitEvent(
    Long id,
    String type,
    String content,
    List<ChatImage> images,
    LocalDateTime sentAt
) {

  public static ChatMessageEmitEvent of(boolean isSystem, String id, String content, List<String> fileNames, LocalDateTime sentAt) {
    Long messageId = parseMessageIdOrNull(id);
    return new ChatMessageEmitEvent(messageId, resolveMessageType(isSystem, id, content, fileNames), content, withSequence(fileNames), sentAt);
  }

  private static Long parseMessageIdOrNull(String id) {
    try {
      return Long.parseLong(id);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static String resolveMessageType(boolean isSystem, String childId, String content, List<String> fileNames) {
    if (isReadAck(childId)) {
      return "READ_ACK";
    }
    if (isSystem) {
      return "SYSTEM";
    }
    if (hasText(content) && hasFiles(fileNames)) {
      return "MIX";
    }
    if (hasText(content)) {
      return "TEXT";
    }
    return "IMAGE";
  }

  private static boolean isReadAck(String childId) {
    return "READ_ACK".equals(childId);
  }

  private static boolean hasText(String content) {
    return content != null && !content.isBlank();
  }

  private static boolean hasFiles(List<String> fileNames) {
    return fileNames != null && !fileNames.isEmpty();
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
