package com.bob.support.fixture.domain.chat;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.entity.type.ChatMessageType;

public class ChatMessageFixture {

  public static ChatMessage DEFAULT_TEXT_CHAT_MESSAGE() {
    return ChatMessage.builder()
        .id(1L)
        .chatRoomId(1L)
        .senderId(MEMBER_ID)
        .content("안녕하세요")
        .type(ChatMessageType.TEXT)
        .build();
  }

  public static ChatMessage WITH_IMAGE_CHAT_MESSAGE() {
    return ChatMessage.builder()
        .id(1L)
        .chatRoomId(1L)
        .senderId(MEMBER_ID)
        .content("사진 포함 채팅")
        .type(ChatMessageType.MIX)
        .build();
  }
}
