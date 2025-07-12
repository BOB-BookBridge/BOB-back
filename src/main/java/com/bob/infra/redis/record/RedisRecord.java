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
    String body,
    List<String> fileNames,
    boolean normalize,
    Sender sender,
    LocalDateTime sentAt
) {

  public static RedisRecord of(
      UUID receiverId, String type, String refId, String body, List<String> fileNames,
      boolean normalize, UUID senderId, String senderNickname, String senderProfile
  ) {
    return RedisRecord.builder()
        .receiverId(receiverId)
        .type(type)
        .refId(refId)
        .body(body)
        .fileNames(fileNames)
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