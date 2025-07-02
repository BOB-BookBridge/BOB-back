package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ReadChatRoomListQuery(
    UUID memberId
) {

  public static ReadChatRoomListQuery of(UUID memberId) {
    return new ReadChatRoomListQuery(memberId);
  }
}
