package com.bob.domain.chat.entity;

import static com.bob.support.fixture.domain.chat.ChatRoomFixture.DEFAULT_CHAT_ROOM_1;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.chat.entity.status.TradeStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("채팅방 도메인 테스트")
class ChatRoomTest {

  @Test
  void 채팅방_거래_상태_변경() {
    // given
    ChatRoom chatRoom = DEFAULT_CHAT_ROOM_1();

    // when
    chatRoom.updateChatRoomTradeStatus(TradeStatus.RESERVED);

    // then
    assertThat(chatRoom.getTradeStatus()).isEqualTo(TradeStatus.RESERVED);
  }

  @Test
  void 채팅방_마지막_메시지_정보_갱신() {
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
  void 채팅방_마지막_메시지_사진_대체_empty() {
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
  void 채팅방_마지막_메시지_사진_대체_null() {
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