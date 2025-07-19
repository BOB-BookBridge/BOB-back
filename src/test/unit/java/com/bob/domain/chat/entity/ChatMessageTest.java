package com.bob.domain.chat.entity;

import static com.bob.domain.chat.entity.type.ChatMessageType.TEXT;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ChatMessage 도메인 테스트")
class ChatMessageTest {

  @Test
  @DisplayName("메시지 읽음 처리 - 성공 테스트")
  void 메시지를_읽음처리_할_수_있다() {
    // given
    ChatMessage chatMessage = ChatMessage.builder()
        .chatRoomId(1L)
        .senderId(UUID.randomUUID())
        .type(TEXT)
        .content("읽지 않은 메시지")
        .isRead(false)
        .build();

    // when
    chatMessage.readMessage();

    // then
    assertThat(chatMessage.getIsRead()).isTrue();
  }
}