package com.bob.global.event.application.dto;

import java.util.UUID;

public record SystemChatMessageEvent(
    String domain,
    String refId,
    UUID memberId,
    UUID partnerId,
    String body
) {

  public static SystemChatMessageEvent of(String domain, String refId, UUID memberId, UUID partnerId, String body) {
    return new SystemChatMessageEvent(domain, refId, memberId, partnerId, body);
  }
}
