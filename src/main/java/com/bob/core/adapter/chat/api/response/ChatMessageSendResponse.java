package com.bob.core.adapter.chat.api.response;

import java.time.LocalDateTime;

import com.bob.core.domain.chat.ChatMessage;

public record ChatMessageSendResponse(long id, boolean isRead, LocalDateTime sentAt) {

    public static ChatMessageSendResponse of(ChatMessage message, Long partnerLastReadMessageId) {
        boolean isRead = partnerLastReadMessageId != null && message.getId() <= partnerLastReadMessageId;

        return new ChatMessageSendResponse(message.getId(), isRead, message.getCreatedAt());
    }
}
