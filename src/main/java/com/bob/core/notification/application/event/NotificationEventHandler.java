package com.bob.core.notification.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bob.core.chat.event.ChatMessageSentEvent;
import com.bob.core.inquiry.event.InquiryProcessedEvent;
import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.application.port.in.NotificationCreator;
import com.bob.core.notification.application.port.out.NotificationMemberPort;
import com.bob.core.report.event.ReportNotificationEvent;
import com.bob.core.trade.event.TradeNotificationEvent;
import com.bob.shared.event.NoticeNotificationEvent;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationCreator notificationCreator;

    private final NotificationMemberPort notificationMemberPort;

    @Async
    @ApplicationModuleListener
    public void handleTradeNotification(TradeNotificationEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromTradeEvent(event);
        notificationCreator.create(command);
    }

    @Async
    @EventListener // 재처리 불필요, 일회성 채팅 알림
    public void handleChatMessageNotification(ChatMessageSentEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromChatEvent(event);
        notificationCreator.create(command);
    }

    @Async
    @ApplicationModuleListener
    public void handleInquiryNotification(InquiryProcessedEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromInquiryEvent(event);
        notificationCreator.create(command);
    }

    @Async
    @ApplicationModuleListener
    public void handleReportNotification(ReportNotificationEvent event) {
        CreateNotificationCommand command = CreateNotificationCommand.fromReportEvent(event);
        notificationCreator.create(command);
    }

    @Async
    @ApplicationModuleListener
    public void handleNoticeNotification(NoticeNotificationEvent event) {
        notificationMemberPort.readAllMemberIds().forEach(memberId -> {
            var command = CreateNotificationCommand.fromNoticeEvent(event, memberId);
            notificationCreator.create(command);
        });
    }
}
