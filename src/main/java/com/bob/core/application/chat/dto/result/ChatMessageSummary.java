package com.bob.core.application.chat.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bob.core.application.chat.port.result.ChatFile;
import com.bob.core.domain.chat.ChatMessage;

public record ChatMessageSummary(
    Long id, String type, String content, List<ChatFile> images, LocalDateTime sentAt,
    boolean isRead, boolean isMine
) {

    public static ChatMessageSummary of(ChatMessage message, UUID memberId, List<ChatFile> files, Long lastReadId) {
        boolean isMine = message.getSenderId().equals(memberId);
        boolean isRead = isRead(message, isMine, lastReadId);

        return new ChatMessageSummary(
            message.getId(), message.getType().name(), message.getContent(), files, message.getCreatedAt(),
            isRead, isMine
        );
    }

    private static boolean isRead(ChatMessage message, boolean isMine, Long lastReadId) {
        if (isMine)
            return true;

        if (lastReadId == null)
            return false;

        return message.getId() <= lastReadId;
    }
}

