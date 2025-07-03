package com.bob.domain.chat.entity;

import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_1;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DISABLE_CHAT_ROOM_1;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅방 도메인 테스트")
class ChatRoomTest {

  @Test
  @DisplayName("채팅방 비활성화 테스트")
  void 채팅방_상태를_비활성화할_수_있다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();

    // when
    chatRoom.updateChatRoomStatus(false);

    // then
    assertThat(chatRoom.getEnableStatus()).isFalse();
  }

  @Test
  @DisplayName("채팅방 상태를 활성화할 수 있다")
  void 채팅방_상태를_활성화할_수_있다() {
    // given
    ChatRoom chatRoom = DISABLE_CHAT_ROOM_1();

    // when
    chatRoom.updateChatRoomStatus(true);

    // then
    assertThat(chatRoom.getEnableStatus()).isTrue();
  }
}