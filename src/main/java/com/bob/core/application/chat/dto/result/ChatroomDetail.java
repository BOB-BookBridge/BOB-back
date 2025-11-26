package com.bob.core.application.chat.dto.result;

import lombok.Builder;

import com.bob.core.application.chat.port.result.ChatMember;
import com.bob.core.application.chat.port.result.ChatPost;
import com.bob.core.domain.chat.Chatroom;

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
