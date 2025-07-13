package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record EnterChatRoomCommand(
    Long chatRoomId,
    UUID memberId
) {

  public static EnterChatRoomCommand of(Long chatRoomId, UUID memberId) {
    return new EnterChatRoomCommand(chatRoomId, memberId);
  }
}
