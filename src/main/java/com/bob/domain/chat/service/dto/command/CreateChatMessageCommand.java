package com.bob.domain.chat.service.dto.command;

import static com.bob.domain.chat.entity.type.ChatMessageType.IMAGE;
import static com.bob.domain.chat.entity.type.ChatMessageType.MIX;
import static com.bob.domain.chat.entity.type.ChatMessageType.SYSTEM;
import static com.bob.domain.chat.entity.type.ChatMessageType.TEXT;

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
  public static final String CHANGED_TRADE_STATUS_PREFIX = "거래 상태가 ";
  public static final String CHANGED_TRADE_STATUS_SUFFIX = " 상태로 변경되었습니다.";

  public static CreateChatMessageCommand of(Long chatRoomId, UUID memberId, String content, List<String> fileNames) {
    return CreateChatMessageCommand.builder()
        .chatRoomId(chatRoomId)
        .memberId(memberId)
        .content(content)
        .fileNames(fileNames)
        .build();
  }

  public static String convertSystemMessage(String body) {
    if (body.equals(IS_FAR_MEMBER)) {
      return body;
    }
    return CHANGED_TRADE_STATUS_PREFIX + body + CHANGED_TRADE_STATUS_SUFFIX;
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
        .content(convertSystemMessage(content))
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
