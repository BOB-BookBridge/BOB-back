package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record ExitChatRoomCommand(
    Long chatRoomId,
    UUID memberId
) {

  public static ExitChatRoomCommand of(Long chatRoomId, UUID memberId) {
    return new ExitChatRoomCommand(chatRoomId, memberId);
  }
}
