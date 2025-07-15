package com.bob.domain.chat.service.dto.response.internal;

import com.bob.domain.chat.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MessageSummary(
    Long id,
    String type,
    String content,
    List<ChatFileSummary> images,
    LocalDateTime sentAt,
    boolean isRead,
    boolean isMine
) {

  public static MessageSummary from(ChatMessage message, UUID memberId, List<ChatFileSummary> files) {
    return new MessageSummary(
        message.getId(),
        message.getType().name(),
        message.getContent(),
        files,
        message.getCreatedAt(),
        message.getIsRead(),
        message.getSenderId().equals(memberId)
    );
  }
}

