package com.bob.core.application.notification.event;

import static com.bob.core.domain.notification.NotificationType.CHAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.application.notification.dto.command.CreateNotificationCommand;
import com.bob.core.application.notification.port.in.NotificationCreator;
import com.bob.global.event.application.dto.NotificationEvent;
import com.bob.global.event.application.dto.type.NotiEventType;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 이벤트 처리 테스트")
class NotificationEventHandlerTest {

    @InjectMocks
    private NotiEventHandler notiEventHandler;

    @Mock
    private NotificationCreator notificationCreator;

    @Test
    void 이벤트_처리() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        NotificationEvent event = NotificationEvent.of(NotiEventType.CHAT, "1", "1", senderId, receiverId, "MESSAGE",
            null, false);

        notiEventHandler.createNotification(event);

        ArgumentCaptor<CreateNotificationCommand> captor = ArgumentCaptor.forClass(CreateNotificationCommand.class);
        then(notificationCreator).should(times(1)).create(captor.capture());
        CreateNotificationCommand command = captor.getValue();
        assertThat(command.type()).isEqualTo(CHAT);
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.senderId()).isEqualTo(senderId);
        assertThat(command.receiverId()).isEqualTo(receiverId);
        assertThat(command.body()).isEqualTo("MESSAGE");
        assertThat(command.normalize()).isFalse();
    }
}
