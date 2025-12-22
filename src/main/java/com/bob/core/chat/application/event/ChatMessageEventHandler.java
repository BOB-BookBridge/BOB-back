package com.bob.core.chat.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bob.core.chat.application.dto.command.CreateSystemMessageCommand;
import com.bob.core.chat.application.port.in.ChatMessageCreator;
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
