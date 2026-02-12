package com.bob.core.notification.application.dto.command;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.chat.event.ChatMessageSentEvent;
import com.bob.core.inquiry.event.InquiryProcessedEvent;
import com.bob.core.notification.domain.NotificationType;
import com.bob.core.report.event.ReportNotificationEvent;
import com.bob.core.trade.event.TradeNotificationEvent;

@Builder
public record CreateNotificationCommand(
    NotificationType type,
    String refId,
    String childId,
    UUID senderId,
    UUID receiverId,
    String body,
    List<String> fileNames,
    boolean isSystem,
    boolean normalize
) {

    public static CreateNotificationCommand fromTradeEvent(TradeNotificationEvent event) {
        return CreateNotificationCommand.builder()
            .type(NotificationType.TRADE)
            .refId(String.valueOf(event.postId()))
            .childId("SYSTEM")
            .senderId(event.senderId())
            .receiverId(event.receiverId())
            .body(event.body())
            .fileNames(null)
            .isSystem(true)
            .normalize(false)
            .build();
    }

    public static CreateNotificationCommand fromChatEvent(ChatMessageSentEvent event) {
        return CreateNotificationCommand.builder()
            .type(NotificationType.CHAT)
            .refId(String.valueOf(event.id()))
            .childId(event.messageId())
            .senderId(event.senderId())
            .receiverId(event.receiverId())
            .body(event.body())
            .fileNames(event.fileNames())
            .isSystem(event.isSystem())
            .normalize(event.normalize())
            .build();
    }

    public static CreateNotificationCommand fromInquiryEvent(InquiryProcessedEvent event) {
        return CreateNotificationCommand.builder()
            .type(NotificationType.INQUIRY)
            .refId(String.valueOf(event.inquiryId()))
            .childId("SYSTEM")
            .senderId(event.memberId())
            .receiverId(event.memberId())
            .body("문의 답변이 도착했습니다. 클릭하여 상세 내용을 확인해 주세요.")
            .fileNames(null)
            .isSystem(true)
            .normalize(false)
            .build();
    }

    public static CreateNotificationCommand fromReportEvent(ReportNotificationEvent event) {
        return CreateNotificationCommand.builder()
            .type(NotificationType.REPORT)
            .refId(String.valueOf(event.reportId()))
            .childId("SYSTEM")
            .senderId(event.reportedId())
            .receiverId(event.reportedId())
            .body("신고 접수에 따라 관련 콘텐츠가 비활성화되었습니다. 자세한 내용은 고객센터를 통해 문의해 주세요.")
            .fileNames(null)
            .isSystem(true)
            .normalize(false)
            .build();
    }
}
