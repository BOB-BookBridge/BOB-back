package com.bob.domain.chat.service.dto.query;

import java.util.UUID;

public record ReadUnreadMessageCountQuery(
    UUID memberId
) {

  public static ReadUnreadMessageCountQuery of(UUID memberId) {
    return new ReadUnreadMessageCountQuery(memberId);
  }
}
