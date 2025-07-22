package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ReadChatMessagesQuery(
    UUID memberId,
    Long chatRoomId
) {

  public static ReadChatMessagesQuery of(UUID memberId, Long chatRoomId) {
    return new ReadChatMessagesQuery(memberId, chatRoomId);
  }
}
