package com.bob.domain.member.service.dto.query;

import java.util.UUID;

public record ReadProfileQuery(
    UUID memberId,
    boolean isMe
) {

  public static ReadProfileQuery of(UUID memberId, boolean isMe) {
    return new ReadProfileQuery(memberId, isMe);
  }
}
