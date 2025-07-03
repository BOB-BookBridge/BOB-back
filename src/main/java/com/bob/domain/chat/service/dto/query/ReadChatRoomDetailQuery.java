package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ReadChatRoomDetailQuery(
    Long chatroomId,
    UUID memberId
) {

  public static ReadChatRoomDetailQuery of(Long chatroomId, UUID memberId) {
    return new ReadChatRoomDetailQuery(chatroomId, memberId);
  }
}
