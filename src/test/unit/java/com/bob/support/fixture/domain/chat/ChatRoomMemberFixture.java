package com.bob.support.fixture.domain.chat;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.chat.entity.ChatRoomMember;
import java.time.LocalDateTime;

public class ChatRoomMemberFixture {

  public static ChatRoomMember CHAT_ROOM_MEMBER_1() {
    return ChatRoomMember.builder()
        .chatRoomId(1L)
        .memberId(MEMBER_ID)
        .exitedAt(null)
        .build();
  }

  public static ChatRoomMember CHAT_ROOM_MEMBER_2() {
    return ChatRoomMember.builder()
        .chatRoomId(1L)
        .memberId(OTHER_MEMBER_ID)
        .exitedAt(null)
        .build();
  }

  public static ChatRoomMember EXITED_CHAT_ROOM_MEMBER() {
    return ChatRoomMember.builder()
        .chatRoomId(1L)
        .memberId(OTHER_MEMBER_ID)
        .exitedAt(LocalDateTime.now().minusHours(1))
        .build();
  }
}
