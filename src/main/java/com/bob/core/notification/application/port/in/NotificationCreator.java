package com.bob.core.notification.application.port.in;

import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.domain.Notification;

public interface NotificationCreator {

    Notification create(CreateNotificationCommand command);
}
