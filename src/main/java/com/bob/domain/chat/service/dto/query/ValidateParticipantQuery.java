package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ValidateParticipantQuery(
    Long chatRoomId,
    UUID memberId
) {

  public static ValidateParticipantQuery of(Long chatRoomId, UUID memberId) {
    return new ValidateParticipantQuery(chatRoomId, memberId);
  }
}
