package com.bob.support.fixture.domain.chat;

import com.bob.domain.chat.entity.ChatRoom;
import java.time.LocalDateTime;

public class ChatRoomFixture {

  public static ChatRoom DEFAULT_CHAT_ROOM_1 =
      ChatRoom.builder()
          .postId(1L)
          .tradeId(1L)
          .titleSuffix("제목")
          .lastChatMessage("안녕하세요")
          .lastChatAt(LocalDateTime.of(2024, 3, 30, 15, 0))
          .enableStatus(true)
          .build();

  public static ChatRoom DEFAULT_CHAT_ROOM_2 =
      ChatRoom.builder()
          .postId(2L)
          .tradeId(2L)
          .titleSuffix("제목")
          .lastChatMessage("거래 감사합니다.")
          .lastChatAt(LocalDateTime.of(2024, 4, 30, 15, 0))
          .enableStatus(true)
          .build();

  public static ChatRoom DISABLE_CHAT_ROOM_1 =
      ChatRoom.builder()
          .postId(3L)
          .tradeId(3L)
          .titleSuffix("제목")
          .lastChatMessage(null)
          .lastChatAt(null)
          .enableStatus(false)
          .build();

  public static ChatRoom customChatRoom(Long id, String lastMessage, LocalDateTime lastMessageAt, boolean status) {
    return ChatRoom.builder()
        .id(id)
        .postId(1L)
        .tradeId(100L)
        .titleSuffix("제목")
        .lastChatMessage(lastMessage)
        .lastChatAt(lastMessageAt)
        .enableStatus(status)
        .build();
  }
}
