package com.bob.global.event.sse.repository.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 Emitter key 반환 테스트")
class ChatEmitterKeyTest {

  @Test
  @DisplayName("문자열 반환 테스트")
  void toString_메서드는_정해진_형식으로_반환한다() {
    // given
    Long chatRoomId = 42L;
    UUID memberId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    ChatEmitterKey key = new ChatEmitterKey(chatRoomId, memberId);

    // when
    String result = key.toString();

    // then
    assertThat(result).isEqualTo("42:123e4567-e89b-12d3-a456-426614174000");
  }
}