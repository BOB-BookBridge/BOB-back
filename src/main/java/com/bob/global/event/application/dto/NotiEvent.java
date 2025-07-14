package com.bob.global.event.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotiEvent(
    String type,
    String refId,
    String childId,
    UUID senderId,
    UUID receiverId,
    String body,
    List<String> fileNames,
    boolean normalize
) {

  public static NotiEvent of(String type, String refId, String childId, UUID senderId, UUID receiverId, String body, List<String> fileNames, boolean normalize) {
    return NotiEvent.builder()
        .type(type)
        .refId(refId)
        .childId(childId)
        .senderId(senderId)
        .receiverId(receiverId)
        .body(body)
        .fileNames(fileNames)
        .normalize(normalize)
        .build();
  }
}
