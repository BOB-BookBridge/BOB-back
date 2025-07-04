package com.bob.domain.chat.service.dto.command;

import com.bob.domain.chat.entity.ChatRoom;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateChatRoomCommand(
    Long postId,
    UUID buyerId
) {

  public ChatRoom toChatRoom(Long tradeId, String titleSuffix) {
    return ChatRoom.builder()
        .postId(postId)
        .tradeId(tradeId)
        .titleSuffix(titleSuffix)
        .enableStatus(false)
        .lastChatAt(null)
        .build();
  }
}