package com.bob.domain.notification.service.dto.command;

import java.util.UUID;

public record ChangeNotificationReadStatusCommand(
    UUID memberId,
    Long notificationId
) {

  public static ChangeNotificationReadStatusCommand of(UUID memberId, Long notificationId) {
    return new ChangeNotificationReadStatusCommand(memberId, notificationId);
  }
}
