package com.bob.infra.redis.record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RedisRecord(
    UUID receiverId,
    String type,
    String refId,
    String childId,
    String body,
    List<String> fileNames,
    boolean isSystem,
    boolean normalize,
    Sender sender,
    LocalDateTime sentAt
) {

  public static RedisRecord of(
      UUID receiverId, String type, String refId, String childId, String body, List<String> fileNames,
      boolean isSystem, boolean normalize, UUID senderId, String senderNickname, String senderProfile
  ) {
    return RedisRecord.builder()
        .receiverId(receiverId)
        .type(type)
        .refId(refId)
        .childId(childId)
        .body(body)
        .fileNames(fileNames)
        .isSystem(isSystem)
        .normalize(normalize)
        .sender(Sender.of(senderId, senderNickname, senderProfile))
        .sentAt(LocalDateTime.now())
        .build();
  }

  public record Sender(
      UUID id,
      String nickname,
      String profile
  ) {

    public static Sender of(UUID id, String nickname, String profile) {
      return new Sender(id, nickname, profile);
    }
  }
}