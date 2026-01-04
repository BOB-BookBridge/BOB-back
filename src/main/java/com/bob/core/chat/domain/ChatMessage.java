package com.bob.core.chat.domain;

import static com.bob.core.chat.domain.type.ChatMessageType.IMAGE;
import static com.bob.core.chat.domain.type.ChatMessageType.MIX;
import static com.bob.core.chat.domain.type.ChatMessageType.SYSTEM;
import static com.bob.core.chat.domain.type.ChatMessageType.TEXT;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.chat.domain.type.ChatMessageType;
import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage extends AbstractEntity {

    private UUID senderId;

    private ChatMessageType type;

    private String content;

    private LocalDateTime createdAt;

    public static ChatMessage createChatMessage(UUID memberId, String content, ChatMessageType type) {
        return ChatMessage.builder()
            .senderId(memberId)
            .content(content)
            .type(type)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static ChatMessage createSystemChatMessage(UUID memberId, String content) {
        return ChatMessage.builder()
            .senderId(memberId)
            .content(content)
            .type(SYSTEM)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static ChatMessageType resolveMessageType(List<String> fileNames, String content) {
        boolean hasMessage = content != null && !content.isBlank();
        boolean hasImage = fileNames != null && !fileNames.isEmpty();

        if (hasMessage && hasImage)
            return MIX;

        if (hasMessage)
            return TEXT;

        return IMAGE;
    }
}
