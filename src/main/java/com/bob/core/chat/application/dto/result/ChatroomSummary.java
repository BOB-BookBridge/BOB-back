package com.bob.core.chat.application.dto.result;

import java.time.LocalDateTime;

import lombok.Builder;

import com.bob.core.chat.application.port.result.ChatMember;
import com.bob.core.chat.application.port.result.ChatPost;
import com.bob.core.chat.domain.Chatroom;

@Builder
public record ChatroomSummary(
    Long id, String thumbnailUrl, String lastMessage, LocalDateTime lastMessageAt, ChatMember partner, int unreadCount
) {

    public static ChatroomSummary of(Chatroom chatroom, ChatMember partner, ChatPost post, int unreadCount) {
        return ChatroomSummary.builder()
            .id(chatroom.getId())
            .thumbnailUrl(post.thumbnailUrl())
            .lastMessage(chatroom.getLastChatMessage())
            .lastMessageAt(chatroom.getLastChatAt())
            .partner(partner)
            .unreadCount(unreadCount)
            .build();
    }
}
