package com.bob.domain.chat.service.dto.command;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.entity.type.ChatMessageType;
import java.util.List;
import java.util.UUID;

public record CreateChatMessageCommand(
    Long chatRoomId,
    UUID memberId,
    String message,
    List<String> fileNames
) {

  public ChatMessage toChatMessage() {
    return ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(memberId)
        .chatMessage(message)
        .chatMessageType(resolveMessageType())
        .build();
  }

  private ChatMessageType resolveMessageType() {
    boolean hasMessage = message != null && !message.isBlank();
    boolean hasImage = fileNames != null && !fileNames.isEmpty();

    if (hasMessage && hasImage) return ChatMessageType.MIX;
    if (hasMessage) return ChatMessageType.MESSAGE;
    return ChatMessageType.IMAGE;
  }
}
