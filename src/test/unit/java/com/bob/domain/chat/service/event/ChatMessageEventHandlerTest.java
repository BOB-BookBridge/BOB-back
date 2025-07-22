package com.bob.domain.chat.service.event;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.bob.domain.chat.service.ChatRoomService;
import com.bob.domain.chat.service.dto.command.CreateSystemMessageCommand;
import com.bob.global.event.application.dto.SystemChatMessageEvent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("채팅 메시지 이벤트 핸들러 테스트")
class ChatMessageEventHandlerTest {

  @InjectMocks
  private ChatMessageEventHandler chatMessageEventHandler;

  @Mock
  private ChatRoomService chatRoomService;

  @Test
  @DisplayName("SystemChatMessageEvent 이벤트 처리 - 성공 테스트")
  void SystemChatMessageEvent_이벤트를_정상적으로_처리할_수_있다() {
    // given
    String domain = "TRADE";
    String refId = "1";
    UUID memberId = MEMBER_ID;
    UUID partnerId = OTHER_MEMBER_ID;
    String body = "예약";
    SystemChatMessageEvent event = new SystemChatMessageEvent(domain, refId, memberId, partnerId, body);
    CreateSystemMessageCommand expectedCommand = CreateSystemMessageCommand.of(
        domain, refId, memberId, partnerId, body
    );
    willDoNothing().given(chatRoomService).createChatRoomSystemMessageProcess(expectedCommand);

    // when
    chatMessageEventHandler.handleSystemChatMessageEvent(event);

    // then
    then(chatRoomService).should().createChatRoomSystemMessageProcess(expectedCommand);
  }
}