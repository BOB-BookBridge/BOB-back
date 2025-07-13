package com.bob.domain.chat.entity;

import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_1;
import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DISABLE_CHAT_ROOM_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
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

  @Test
  @DisplayName("채팅방의 마지막 메시지 정보 갱신 - 일반 메시지")
  void 채팅방_마지막_메시지_정보를_갱신할_수_있다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();
    String newMessage = "안녕하세요";
    LocalDateTime now = LocalDateTime.now();

    // when
    chatRoom.updateChatRoomLastMessageInfo(newMessage, now);

    // then
    assertThat(chatRoom.getLastChatMessage()).isEqualTo("안녕하세요");
    assertThat(chatRoom.getLastChatAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("채팅방의 마지막 메시지 정보 갱신 - 메시지가 빈 값이면 '사진'으로 대체된다")
  void 채팅방_마지막_메시지가_빈값이면_사진으로_대체된다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();
    String emptyMessage = "";
    LocalDateTime now = LocalDateTime.now();

    // when
    chatRoom.updateChatRoomLastMessageInfo(emptyMessage, now);

    // then
    assertThat(chatRoom.getLastChatMessage()).isEqualTo("사진");
    assertThat(chatRoom.getLastChatAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("채팅방의 마지막 메시지 정보 갱신 - 메시지가 null 값이면 '사진'으로 대체된다")
  void 채팅방_마지막_메시지가_null값이면_사진으로_대체된다() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();
    String nullMessage = null;
    LocalDateTime now = LocalDateTime.now();

    // when
    chatRoom.updateChatRoomLastMessageInfo(nullMessage, now);

    // then
    assertThat(chatRoom.getLastChatMessage()).isEqualTo("사진");
    assertThat(chatRoom.getLastChatAt()).isEqualTo(now);
  }
}