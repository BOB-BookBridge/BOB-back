package com.bob.domain.notification.service.dto.command;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.entity.NotificationType;
import com.bob.global.event.application.dto.NotiEvent;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateNotiCommand(
    NotificationType type,
    String refId,
    String childId,
    UUID senderId,
    UUID receiverId,
    String body,
    List<String> fileNames,
    boolean isSystem,
    boolean normalize
) {

  public static CreateNotiCommand of(
      String type, String refId, String childId, UUID senderId, UUID receiverId,
      String body, List<String> fileNames, boolean isSystem, boolean normalize
  ) {
    return CreateNotiCommand.builder()
        .refId(refId)
        .childId(childId)
        .type(NotificationType.valueOf(type))
        .senderId(senderId)
        .receiverId(receiverId)
        .body(body)
        .fileNames(fileNames)
        .isSystem(isSystem)
        .normalize(normalize)
        .build();
  }

  public static CreateNotiCommand fromEvent(NotiEvent event) {
    return CreateNotiCommand.builder()
        .refId(event.refId())
        .childId(event.childId())
        .type(NotificationType.valueOf(event.type().name()))
        .senderId(event.senderId())
        .receiverId(event.receiverId())
        .body(event.body())
        .fileNames(event.fileNames())
        .isSystem(event.isSystem())
        .normalize(event.normalize())
        .build();
  }

  public Notification toTradeNotiEntity(String sender) {
    return Notification.builder()
        .referenceId(refId)
        .type(type)
        .receiverId(receiverId)
        .body(sender + "님과의 거래 상태가 '" + body + "'상태로 변경되었습니다.")
        .build();
  }
}