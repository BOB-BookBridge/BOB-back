package com.bob.domain.chat.service.dto.response;

public record ChatMessageSendResponse(
    boolean isRead
) {

  public static ChatMessageSendResponse of(boolean isRead) {
    return new ChatMessageSendResponse(isRead);
  }
}
