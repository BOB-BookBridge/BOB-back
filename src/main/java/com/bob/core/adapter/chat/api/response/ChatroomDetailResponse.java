package com.bob.core.adapter.chat.api.response;

import java.util.UUID;

import lombok.Builder;

import com.bob.core.application.chat.dto.result.ChatroomDetail;

@Builder
public record ChatroomDetailResponse(Long id, String title, ChatTrade trade, ChatPost post, Partner partner) {

    public static ChatroomDetailResponse of(ChatroomDetail room, String tradeStatus) {
        return ChatroomDetailResponse.builder()
            .id(room.id())
            .title(room.title())
            .trade(new ChatTrade(room.tradeId(), tradeStatus))
            .post(new ChatPost(
                room.post().id(), room.post().tradeStatus(), room.post().sellerId(), room.post().title(),
                room.post().thumbnailUrl(), room.post().sellPrice()
            ))
            .partner(new Partner(room.partner().id(), room.partner().nickname(), room.partner().profileImageUrl()))
            .build();
    }

    public record ChatTrade(Long id, String status) {

    }

    public record ChatPost(Long id, String status, UUID sellerId, String title, String thumbnailUrl, int sellPrice) {

    }

    public record Partner(UUID id, String nickname, String profileImageUrl) {

    }
}
