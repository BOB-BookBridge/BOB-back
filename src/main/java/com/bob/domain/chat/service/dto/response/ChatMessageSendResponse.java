package com.bob.domain.chat.service.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageSendResponse(
    long id,
    boolean isRead,
    LocalDateTime sentAt
) {

  public static ChatMessageSendResponse of(long messageId, boolean isRead, LocalDateTime sentAt) {
    return ChatMessageSendResponse.builder()
        .id(messageId)
        .isRead(isRead)
        .sentAt(sentAt)
        .build();
  }
}
