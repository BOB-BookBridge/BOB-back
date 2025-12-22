package com.bob.core.notification.application.port.in;

import java.util.List;

import com.bob.core.notification.application.dto.command.MarkAsReadCommand;
import com.bob.core.notification.domain.Notification;

public interface NotificationMarker {

    Notification markAsRead(Long notificationId, MarkAsReadCommand command);

    List<Notification> markAllAsRead(MarkAsReadCommand command);
}
