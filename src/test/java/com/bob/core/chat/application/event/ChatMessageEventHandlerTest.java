package com.bob.core.chat.application.event;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.chat.application.dto.command.CreateSystemMessageCommand;
import com.bob.core.chat.application.port.in.ChatMessageCreator;
import com.bob.global.event.application.dto.SystemChatMessageEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("채팅 메시지 이벤트 핸들러 테스트")
class ChatMessageEventHandlerTest {

    @InjectMocks
    private ChatMessageEventHandler chatMessageEventHandler;

    @Mock
    private ChatMessageCreator chatMessageCreator;

    @Test
    void 시스템_메시지_이벤트_처리() {
        var event = new SystemChatMessageEvent("TRADE", "1", MEMBER_ID, OTHER_MEMBER_ID, "시스템메시지");

        chatMessageEventHandler.handleSystemChatMessageEvent(event);

        var captor = ArgumentCaptor.forClass(CreateSystemMessageCommand.class);

        then(chatMessageCreator).should().createSystemChatMessage(captor.capture());

        CreateSystemMessageCommand command = captor.getValue();
        assertThat(command.domain()).isEqualTo("TRADE");
        assertThat(command.refId()).isEqualTo("1");
        assertThat(command.senderId()).isEqualTo(MEMBER_ID);
        assertThat(command.partnerId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(command.body()).isEqualTo("시스템메시지");
    }
}
