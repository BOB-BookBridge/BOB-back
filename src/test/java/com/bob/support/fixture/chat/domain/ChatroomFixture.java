package com.bob.support.fixture.chat.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.core.domain.chat.Chatroom;

public class ChatroomFixture {

    public static Chatroom createChatroom() {
        return Chatroom.createChatroom(1L, 1L, "테스트", MEMBER_ID, OTHER_MEMBER_ID);
    }
}
