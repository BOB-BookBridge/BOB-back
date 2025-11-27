package com.bob.core.application.chat.dto.result;

import com.bob.core.domain.chat.ChatMessage;

public record ChatMessageCreationResult(ChatMessage message, Long partnerLastReadMessageId) {

    public static ChatMessageCreationResult of(ChatMessage message, Long partnerLastReadMessageId) {
        return new ChatMessageCreationResult(message, partnerLastReadMessageId);
    }
}
