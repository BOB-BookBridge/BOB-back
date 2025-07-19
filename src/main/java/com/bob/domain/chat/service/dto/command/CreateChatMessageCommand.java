package com.bob.domain.chat.service.dto.command;

import static com.bob.domain.chat.entity.type.ChatMessageType.IMAGE;
import static com.bob.domain.chat.entity.type.ChatMessageType.TEXT;
import static com.bob.domain.chat.entity.type.ChatMessageType.MIX;
import static com.bob.domain.chat.entity.type.ChatMessageType.SYSTEM;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.entity.type.ChatMessageType;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateChatMessageCommand(
    Long chatRoomId,
    UUID memberId,
    String content,
    List<String> fileNames
) {

  public static final String IS_FAR_MEMBER = "거리가 먼 사용자와의 채팅입니다.";

  public static CreateChatMessageCommand of(Long chatRoomId, UUID memberId, String content, List<String> fileNames) {
    return CreateChatMessageCommand.builder()
        .chatRoomId(chatRoomId)
        .memberId(memberId)
        .content(content)
        .fileNames(fileNames)
        .build();
  }

  public ChatMessage toChatMessage() {
    return ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(memberId)
        .content(content)
        .type(resolveMessageType())
        .build();
  }

  public ChatMessage toSystemChatMessage() {
    return ChatMessage.builder()
        .chatRoomId(chatRoomId)
        .senderId(memberId)
        .content(content)
        .type(SYSTEM)
        .isRead(true)
        .build();
  }

  private ChatMessageType resolveMessageType() {
    boolean hasMessage = content != null && !content.isBlank();
    boolean hasImage = fileNames != null && !fileNames.isEmpty();

    if (hasMessage && hasImage) {
      return MIX;
    }
    if (hasMessage) {
      return TEXT;
    }
    return IMAGE;
  }
}
