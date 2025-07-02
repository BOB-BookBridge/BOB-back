package com.bob.domain.chat.service.dto.response;

import com.bob.domain.chat.entity.ChatRoom;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ChatRoomSummaryResponse(
    Long chatroomId,
    String thumbnailUrl,
    String lastMessage,
    LocalDateTime lastMessageAt,
    PartnerInfo partner,
    int unreadCount
) {

  public static ChatRoomSummaryResponse from(
      ChatRoom chatRoom,
      ChatMemberResponse partner, ChatPostResponse post,
      int unreadCount
  ) {
    return ChatRoomSummaryResponse.builder()
        .chatroomId(chatRoom.getId())
        .thumbnailUrl(post.thumbnailUrl())
        .lastMessage(chatRoom.getLastChatMessage())
        .lastMessageAt(chatRoom.getLastChatAt())
        .partner(PartnerInfo.of(partner.memberId(), partner.nickname(), partner.profileImageUrl()))
        .unreadCount(unreadCount)
        .build();
  }

  public record PartnerInfo(
      UUID partnerId,
      String nickname,
      String profileUrl
  ) {

    static PartnerInfo of(UUID partnerId, String nickname, String profileUrl) {
      return new PartnerInfo(partnerId, nickname, profileUrl);
    }
  }
}
