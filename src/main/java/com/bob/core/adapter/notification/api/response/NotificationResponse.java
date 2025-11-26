package com.bob.core.adapter.notification.api.response;

import java.time.LocalDateTime;

import lombok.Builder;

import com.bob.core.domain.notification.Notification;

@Builder
public record NotificationResponse(
    Long id,
    String type,
    String refId,
    String body,
    boolean isRead,
    LocalDateTime createdAt
) {

    public static NotificationResponse of(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .type(notification.getType().name())
            .refId(notification.getReferenceId())
            .body(notification.getBody())
            .isRead(notification.getIsRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}
