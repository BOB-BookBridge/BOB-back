package com.bob.core.chat.application.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bob.core.chat.application.dto.command.CreateSystemMessageCommand;
import com.bob.core.chat.application.port.in.ChatMessageCreator;
import com.bob.core.trade.event.TradeChangedEvent;

@Component
@RequiredArgsConstructor
public class ChatMessageEventHandler {

    private final ChatMessageCreator messageCreator;

    @EventListener
    public void handleTradeChanged(TradeChangedEvent event) {
        CreateSystemMessageCommand command = CreateSystemMessageCommand.fromTradeEvent(event);

        messageCreator.createSystemChatMessage(command);
    }
}
