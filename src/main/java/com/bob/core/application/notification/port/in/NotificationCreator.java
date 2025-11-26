package com.bob.core.application.notification.port.in;

import com.bob.core.application.notification.dto.command.CreateNotificationCommand;
import com.bob.core.domain.notification.Notification;

public interface NotificationCreator {

    Notification create(CreateNotificationCommand command);
}
