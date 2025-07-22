package com.bob.global.event.application.dto;

import com.bob.global.event.application.dto.type.NotiEventType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotiEvent(
    NotiEventType type,
    String refId,
    String childId,
    UUID senderId,
    UUID receiverId,
    String body,
    List<String> fileNames,
    boolean isSystem,
    boolean normalize
) {

  public static NotiEvent of(NotiEventType type, String refId, String childId, UUID senderId, UUID receiverId, String body, List<String> fileNames, boolean normalize) {
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

  public static NotiEvent toSystemNotiEvent(NotiEventType type, String refId, String childId, UUID senderId, UUID receiverId, String body) {
    return NotiEvent.builder()
        .type(type)
        .refId(refId)
        .childId(childId)
        .senderId(senderId)
        .receiverId(receiverId)
        .body(body)
        .fileNames(null)
        .isSystem(true)
        .normalize(false)
        .build();
  }
}
