package com.bob.domain.notification.service.event;

import static com.bob.domain.notification.entity.NotificationType.CHAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.notification.service.NotiService;
import com.bob.domain.notification.service.dto.command.CreateNotiCommand;
import com.bob.global.event.application.dto.NotiEvent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotiEventHandler 테스트")
class NotiEventHandlerTest {

  @InjectMocks
  private NotiEventHandler notiEventHandler;

  @Mock
  private NotiService notiService;

  @Test
  @DisplayName("NotiEvent 수신 - 알림 생성 기능 호출 테스트")
  void NotiEvent를_수신하면_createNotificationProcess가_호출된다() {
    // given
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    NotiEvent event = NotiEvent.of("CHAT", "1", senderId, receiverId, "MESSAGE", false);

    // when
    notiEventHandler.handleNotiEvent(event);

    // then
    ArgumentCaptor<CreateNotiCommand> captor = ArgumentCaptor.forClass(CreateNotiCommand.class);
    then(notiService).should(times(1)).createNotificationProcess(captor.capture());
    CreateNotiCommand command = captor.getValue();
    assertThat(command.type()).isEqualTo(CHAT);
    assertThat(command.refId()).isEqualTo("1");
    assertThat(command.senderId()).isEqualTo(senderId);
    assertThat(command.receiverId()).isEqualTo(receiverId);
    assertThat(command.body()).isEqualTo("MESSAGE");
    assertThat(command.normalize()).isFalse();
  }
}
