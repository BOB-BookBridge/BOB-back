package com.bob.core.application.notification.port.in;

import java.util.List;

import com.bob.core.application.notification.dto.command.MarkAsReadCommand;
import com.bob.core.domain.notification.Notification;

public interface NotificationMarker {

    Notification markAsRead(Long notificationId, MarkAsReadCommand command);

    List<Notification> markAllAsRead(MarkAsReadCommand command);
}
