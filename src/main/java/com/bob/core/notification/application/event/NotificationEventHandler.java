package com.bob.core.notification.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bob.core.chat.event.ChatMessageSentEvent;
import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.application.port.in.NotificationCreator;
import com.bob.core.trade.event.TradeNotificationEvent;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationCreator notificationCreator;

    @Async
    @EventListener
    public void handleTradeNotification(TradeNotificationEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromTradeEvent(event);
        notificationCreator.create(command);
    }

    @Async
    @EventListener
    public void handleChatMessageNotification(ChatMessageSentEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromChatEvent(event);
        notificationCreator.create(command);
    }
}
