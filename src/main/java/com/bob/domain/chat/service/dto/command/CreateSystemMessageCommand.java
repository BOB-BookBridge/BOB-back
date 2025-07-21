package com.bob.domain.chat.service.dto.command;

import java.util.UUID;

public record CreateSystemMessageCommand(
    String domain,
    String refId,
    UUID senderId,
    UUID partnerId,
    String body
) {

  public static CreateSystemMessageCommand of(String domain, String refId, UUID senderId, UUID partnerId, String body) {
    return new CreateSystemMessageCommand(domain, refId, senderId, partnerId, body);
  }
}
