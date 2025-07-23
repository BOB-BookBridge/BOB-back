package com.bob.domain.notification.service.dto.response.internal;

import com.bob.domain.notification.entity.Notification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationSummary(
    Long id,
    String type,
    String refId,
    String body,
    boolean isRead,
    LocalDateTime createdAt
) {

  public static NotificationSummary from(Notification notification) {
    return NotificationSummary.builder()
        .id(notification.getId())
        .type(notification.getType().name())
        .refId(notification.getReferenceId())
        .body(notification.getBody())
        .isRead(notification.getIsRead())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
