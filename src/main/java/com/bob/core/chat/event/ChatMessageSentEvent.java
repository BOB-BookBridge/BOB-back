package com.bob.core.chat.event;

import java.util.List;
import java.util.UUID;

public record ChatMessageSentEvent(
    Long id, String messageId, UUID senderId, UUID receiverId, String body, List<String> fileNames,
    boolean isSystem, boolean normalize
) {

    public static ChatMessageSentEvent of(
        Long id, String messageId, UUID senderId, UUID receiverId, String body,
        List<String> fileNames, boolean normalize
    ) {
        return new ChatMessageSentEvent(id, messageId, senderId, receiverId, body, fileNames, false, normalize);
    }

    public static ChatMessageSentEvent toSystemEvent(Long id, UUID senderId, UUID receiverId, String body) {
        return new ChatMessageSentEvent(id, "SYSTEM", senderId, receiverId, body, null, true, false);
    }
}
