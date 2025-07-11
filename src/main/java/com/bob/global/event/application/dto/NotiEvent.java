package com.bob.global.event.application.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record NotiEvent(
    String type,
    String refId,
    UUID senderId,
    UUID receiverId,
    String body,
    boolean normalize
) {

  public static NotiEvent of(String type, String refId, UUID senderId, UUID receiverId, String body, boolean normalize) {
    return NotiEvent.builder()
        .type(type)
        .refId(refId)
        .senderId(senderId)
        .receiverId(receiverId)
        .body(body)
        .normalize(normalize)
        .build();
  }
}
