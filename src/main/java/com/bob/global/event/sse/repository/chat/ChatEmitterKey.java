package com.bob.global.event.sse.repository.chat;

import java.util.UUID;

public record ChatEmitterKey(
    Long chatRoomId,
    UUID memberId
) {

  public static ChatEmitterKey of(Long chatRoomId, UUID memberId) {
    return new ChatEmitterKey(chatRoomId, memberId);
  }

  @Override
  public String toString() {
    return "chat:" + chatRoomId + ":" + memberId;
  }
}
