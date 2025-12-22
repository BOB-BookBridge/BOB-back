package com.bob.core.notification.application.dto.command;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.notification.domain.NotificationType;
import com.bob.global.event.application.dto.NotificationEvent;

@Builder
public record CreateNotificationCommand(
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

    public static CreateNotificationCommand fromEvent(NotificationEvent event) {
        return CreateNotificationCommand.builder()
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
}
