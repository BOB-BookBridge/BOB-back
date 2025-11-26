package com.bob.core.application.notification.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bob.core.application.notification.dto.command.CreateNotificationCommand;
import com.bob.core.application.notification.port.in.NotificationCreator;
import com.bob.global.event.application.dto.NotificationEvent;

@RequiredArgsConstructor
@Component
public class NotiEventHandler {

    private final NotificationCreator notificationCreator;

    @Async
    @EventListener
    public void createNotification(NotificationEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromEvent(event);

        notificationCreator.create(command);
    }
}
