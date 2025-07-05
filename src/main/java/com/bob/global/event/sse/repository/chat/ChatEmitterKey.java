package com.bob.global.event.sse.repository.chat;

import java.util.UUID;

public record ChatEmitterKey(
    Long chatRoomId,
    UUID memberId
) {

  @Override
  public String toString() {
    return "chat:" + chatRoomId + ":" + memberId;
  }
}
