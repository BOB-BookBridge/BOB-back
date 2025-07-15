package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record ReEnterChatRoomCommand(
    Long chatRoomId,
    UUID buyerId
) {

  public static ReEnterChatRoomCommand of(Long chatRoomId, UUID buyerId) {
    return new ReEnterChatRoomCommand(chatRoomId, buyerId);
  }
}
