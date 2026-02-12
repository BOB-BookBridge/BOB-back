package com.bob.core.chat.application.event;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bob.core.chat.application.dto.command.DeactivateChatroomCommand;
import com.bob.core.chat.application.port.in.ChatroomModifier;
import com.bob.core.report.event.ReportChatProcessedEvent;

@DisplayName("채팅방 이벤트 처리 테스트")
@ExtendWith(MockitoExtension.class)
class ChatroomEventHandlerTest {

    @InjectMocks
    private ChatroomEventHandler chatroomEventHandler;

    @Mock
    private ChatroomModifier chatroomModifier;

    @Test
    void 채팅방_신고_이벤트_처리() {
        Long chatMessageId = 1L;
        var event = new ReportChatProcessedEvent(chatMessageId);

        chatroomEventHandler.handleChatReportProcessed(event);

        var captor = ArgumentCaptor.forClass(DeactivateChatroomCommand.class);

        then(chatroomModifier).should(times(1)).deactivate(captor.capture());
    }
}
