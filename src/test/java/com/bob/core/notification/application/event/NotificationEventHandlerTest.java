package com.bob.core.notification.application.event;

import static com.bob.core.notification.domain.NotificationType.CHAT;
import static com.bob.core.notification.domain.NotificationType.INQUIRY;
import static com.bob.core.notification.domain.NotificationType.REPORT;
import static com.bob.core.notification.domain.NotificationType.TRADE;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.chat.event.ChatMessageSentEvent;
import com.bob.core.inquiry.event.InquiryProcessedEvent;
import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.application.port.in.NotificationCreator;
import com.bob.core.report.event.ReportNotificationEvent;
import com.bob.core.trade.event.TradeNotificationEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 이벤트 처리 테스트")
class NotificationEventHandlerTest {

    @InjectMocks
    private NotificationEventHandler notificationEventHandler;

    @Mock
    private NotificationCreator notificationCreator;

    @Test
    void 거래_알림_이벤트_처리() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        TradeNotificationEvent event = new TradeNotificationEvent(1L, senderId, receiverId, "거래 요청이 도착했습니다.");

        notificationEventHandler.handleTradeNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());
        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(TRADE);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.childId()).isEqualTo("SYSTEM");
        assertThat(command.senderId()).isEqualTo(senderId);
        assertThat(command.receiverId()).isEqualTo(receiverId);
        assertThat(command.body()).isEqualTo("거래 요청이 도착했습니다.");
        assertThat(command.isSystem()).isTrue();
        assertThat(command.normalize()).isFalse();
    }

    @Test
    void 채팅_메시지_알림_이벤트_처리() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatMessageSentEvent event = ChatMessageSentEvent.of(
            1L, "123", senderId, receiverId, "안녕하세요", List.of("image1.jpg"), true
        );

        notificationEventHandler.handleChatMessageNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());
        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(CHAT);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.childId()).isEqualTo("123");
        assertThat(command.senderId()).isEqualTo(senderId);
        assertThat(command.receiverId()).isEqualTo(receiverId);
        assertThat(command.body()).isEqualTo("안녕하세요");
        assertThat(command.fileNames()).containsExactly("image1.jpg");
        assertThat(command.isSystem()).isFalse();
        assertThat(command.normalize()).isTrue();
    }

    @Test
    void 시스템_채팅_메시지_알림_이벤트_처리() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ChatMessageSentEvent event = ChatMessageSentEvent.toSystemEvent(
            1L, senderId, receiverId, "거래 상태가 변경되었습니다."
        );

        notificationEventHandler.handleChatMessageNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());
        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(CHAT);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.childId()).isEqualTo("SYSTEM");
        assertThat(command.senderId()).isEqualTo(senderId);
        assertThat(command.receiverId()).isEqualTo(receiverId);
        assertThat(command.body()).isEqualTo("거래 상태가 변경되었습니다.");
        assertThat(command.fileNames()).isNull();
        assertThat(command.isSystem()).isTrue();
        assertThat(command.normalize()).isFalse();
    }

    @Test
    void 문의_처리_알림_이벤트_처리() {
        UUID receiverId = MEMBER_ID;
        InquiryProcessedEvent event = new InquiryProcessedEvent(1L, receiverId);

        notificationEventHandler.handleInquiryNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());

        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(INQUIRY);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.childId()).isEqualTo("SYSTEM");
        assertThat(command.receiverId()).isEqualTo(receiverId);
        assertThat(command.body()).isEqualTo("문의 답변이 도착했습니다. 클릭하여 상세 내용을 확인해 주세요.");
        assertThat(command.fileNames()).isNull();
        assertThat(command.isSystem()).isTrue();
        assertThat(command.normalize()).isFalse();
    }

    @Test
    void 신고_처리_알림_이벤트_처리() {
        UUID reportedId = MEMBER_ID;
        ReportNotificationEvent event = new ReportNotificationEvent(1L, reportedId);

        notificationEventHandler.handleReportNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());

        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(REPORT);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.childId()).isEqualTo("SYSTEM");
        assertThat(command.receiverId()).isEqualTo(reportedId);
        assertThat(command.body()).contains("비활성화");
        assertThat(command.fileNames()).isNull();
        assertThat(command.isSystem()).isTrue();
        assertThat(command.normalize()).isFalse();
    }
}
