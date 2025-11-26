package com.bob.global.event.application.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.global.event.application.dto.type.NotiEventType;

@Builder
public record NotificationEvent(
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

    public static NotificationEvent of(
        NotiEventType type, String refId, String childId,
        UUID senderId, UUID receiverId, String body, List<String> fileNames, boolean normalize
    ) {
        return NotificationEvent.builder()
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

    public static NotificationEvent toSystemEvent(
        NotiEventType type, String refId, String childId, UUID senderId, UUID receiverId, String body
    ) {
        return NotificationEvent.builder()
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
