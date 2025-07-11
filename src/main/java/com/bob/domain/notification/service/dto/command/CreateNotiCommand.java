package com.bob.domain.notification.service.dto.command;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.entity.NotificationType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateNotiCommand(
    NotificationType type,
    String refId,
    UUID senderId,
    UUID receiverId,
    String body,
    boolean normalize
) {

  public static CreateNotiCommand of(String type, String refId, UUID senderId, UUID receiverId, String body, boolean normalize) {
    return CreateNotiCommand.builder()
        .refId(refId)
        .type(NotificationType.valueOf(type))
        .senderId(senderId)
        .receiverId(receiverId)
        .body(body)
        .normalize(normalize)
        .build();
  }

  public Notification toEntity() {
    return Notification.builder()
        .referenceId(refId)
        .type(type)
        .receiverId(receiverId)
        .body(body)
        .build();
  }
}