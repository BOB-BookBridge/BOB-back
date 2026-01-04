package com.bob.core.notification.application.event;

import static com.bob.core.notification.domain.NotificationType.CHAT;
import static com.bob.core.notification.domain.NotificationType.TRADE;
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
import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.application.port.in.NotificationCreator;
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
}
