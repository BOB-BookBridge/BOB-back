package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ReadChatMessagesQuery(
    UUID memberId,
    Long chatRoomId,
    Long beforeMessageId,
    Integer size
) {

  public static ReadChatMessagesQuery of(UUID memberId, Long chatRoomId, Long beforeMessageId, Integer size) {
    return new ReadChatMessagesQuery(memberId, chatRoomId, beforeMessageId, size);
  }
}
