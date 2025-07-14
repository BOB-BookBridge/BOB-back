package com.bob.domain.chat.service.dto.response;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.service.dto.response.internal.ChatPartnerSummary;
import com.bob.domain.chat.service.dto.response.internal.ChatPostSummary;
import com.bob.domain.chat.service.dto.response.internal.ChatTradeSummary;
import lombok.Builder;

@Builder
public record ChatRoomDetailResponse(
    Long chatroomId,
    String title,
    ChatTradeSummary trade,
    ChatPostSummary post,
    ChatPartnerSummary partner
) {

  public static ChatRoomDetailResponse from(
      ChatRoom chatRoom,
      ChatPostResponse post,
      ChatTradeResponse trade,
      ChatMemberResponse partner
  ) {
    return ChatRoomDetailResponse.builder()
        .chatroomId(chatRoom.getId())
        .title(partner.nickname() + " - [" + post.title() + "]")
        .trade(ChatTradeSummary.of(trade.id(), trade.status()))
        .post(ChatPostSummary.of(post.postId(), post.postStatus() ,post.sellerId(), post.title(), post.thumbnailUrl(), post.sellPrice()))
        .partner(ChatPartnerSummary.of(partner.memberId(), partner.nickname(), partner.profileImageUrl()))
        .build();
  }
}