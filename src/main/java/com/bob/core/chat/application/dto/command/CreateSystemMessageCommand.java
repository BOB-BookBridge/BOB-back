package com.bob.core.chat.application.dto.command;

import java.util.UUID;

import com.bob.core.trade.event.TradeChangedEvent;

public record CreateSystemMessageCommand(String domain, String refId, UUID senderId, UUID partnerId, String body) {

    public static CreateSystemMessageCommand fromTradeEvent(TradeChangedEvent event) {
        return new CreateSystemMessageCommand(
            "TRADE", String.valueOf(event.postId()), event.senderId(), event.receiverId(), event.body()
        );
    }
}
