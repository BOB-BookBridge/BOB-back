package com.bob.domain.chat.service.dto.response;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.service.dto.response.internal.ChatPartnerSummary;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatRoomSummaryResponse(
    Long chatroomId,
    String thumbnailUrl,
    String lastMessage,
    LocalDateTime lastMessageAt,
    ChatPartnerSummary partner,
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
        .partner(ChatPartnerSummary.of(partner.memberId(), partner.nickname(), partner.profileImageUrl()))
        .unreadCount(unreadCount)
        .build();
  }
}
