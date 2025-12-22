package com.bob.core.chat.application.dto.result;

import lombok.Builder;

import com.bob.core.chat.application.port.result.ChatMember;
import com.bob.core.chat.application.port.result.ChatPost;
import com.bob.core.chat.domain.Chatroom;

@Builder
public record ChatroomDetail(Long id, String title, Long tradeId, ChatPost post, ChatMember partner) {

    public static ChatroomDetail of(Chatroom chatroom, ChatPost post, ChatMember partner) {
        return ChatroomDetail.builder()
            .id(chatroom.getId())
            .title(partner.nickname() + " - [" + post.title() + "]")
            .tradeId(chatroom.getTradeId())
            .post(post)
            .partner(partner)
            .build();
    }
}
