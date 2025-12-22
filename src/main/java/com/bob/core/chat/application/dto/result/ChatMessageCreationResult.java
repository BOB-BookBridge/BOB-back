package com.bob.core.chat.application.dto.result;

import com.bob.core.chat.domain.ChatMessage;

public record ChatMessageCreationResult(ChatMessage message, Long partnerLastReadMessageId) {

    public static ChatMessageCreationResult of(ChatMessage message, Long partnerLastReadMessageId) {
        return new ChatMessageCreationResult(message, partnerLastReadMessageId);
    }
}
