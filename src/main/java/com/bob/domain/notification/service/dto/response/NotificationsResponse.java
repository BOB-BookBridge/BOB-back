package com.bob.domain.notification.service.dto.response;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.service.dto.response.internal.NotificationSummary;
import java.util.List;

public record NotificationsResponse(
    List<NotificationSummary> notifications
) {

  public static NotificationsResponse from(List<Notification> notifications) {
    return new NotificationsResponse(
        notifications.stream()
            .map(NotificationSummary::from)
            .toList()
    );
  }
}
