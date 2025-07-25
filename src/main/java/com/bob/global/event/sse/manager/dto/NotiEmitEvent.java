package com.bob.global.event.sse.manager.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotiEmitEvent(
    String type,
    String refId,
    String body,
    Sender sender,
    LocalDateTime sentAt
) {

  public static NotiEmitEvent of(String type, String refId, String body, Sender sender, LocalDateTime sentAt) {
    return new NotiEmitEvent(type, refId, body, sender, sentAt);
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