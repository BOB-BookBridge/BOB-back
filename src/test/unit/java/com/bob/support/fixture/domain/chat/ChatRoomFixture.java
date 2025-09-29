package com.bob.support.fixture.domain.chat;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.entity.status.TradeStatus;
import java.time.LocalDateTime;

public class ChatRoomFixture {

  public static ChatRoom DEFAULT_CHAT_ROOM_1() {
    return ChatRoom.builder()
        .id(1L)
        .postId(1L)
        .tradeId(1L)
        .titleSuffix("제목")
        .lastChatMessage("안녕하세요")
        .lastChatAt(LocalDateTime.of(2024, 3, 30, 15, 0))
        .tradeStatus(TradeStatus.ACCEPTED)
        .build();
  }


  public static ChatRoom DEFAULT_CHAT_ROOM_2() {
    return ChatRoom.builder()
        .postId(1L)
        .tradeId(2L)
        .titleSuffix("제목")
        .lastChatMessage("거래 감사합니다.")
        .lastChatAt(LocalDateTime.of(2024, 4, 30, 15, 0))
        .tradeStatus(TradeStatus.ACCEPTED)
        .build();
  }

  public static ChatRoom CANCELED_CHAT_ROOM_1() {
    return ChatRoom.builder()
        .id(1L)
        .postId(1L)
        .tradeId(3L)
        .titleSuffix("제목")
        .lastChatMessage(null)
        .lastChatAt(null)
        .tradeStatus(TradeStatus.CANCELED)
        .build();
  }

  public static ChatRoom customChatRoom(Long id, String lastMessage, LocalDateTime lastMessageAt, TradeStatus status) {
    return ChatRoom.builder()
        .id(id)
        .postId(1L)
        .tradeId(100L)
        .titleSuffix("제목")
        .lastChatMessage(lastMessage)
        .lastChatAt(lastMessageAt)
        .tradeStatus(status)
        .build();
  }
}
