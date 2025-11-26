package com.bob.core.application.chat.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bob.core.application.chat.dto.command.CreateSystemMessageCommand;
import com.bob.core.application.chat.port.in.ChatMessageCreator;
import com.bob.global.event.application.dto.SystemChatMessageEvent;

@Component
@RequiredArgsConstructor
public class ChatMessageEventHandler {

    private final ChatMessageCreator messageCreator;

    @EventListener
    public void handleSystemChatMessageEvent(SystemChatMessageEvent event) {
        CreateSystemMessageCommand command = new CreateSystemMessageCommand(event.domain(), event.refId(),
            event.memberId(), event.partnerId(), event.body());

        messageCreator.createSystemChatMessage(command);
    }
}
