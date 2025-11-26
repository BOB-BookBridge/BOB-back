package com.bob.support.fixture.chat.dto.query;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.UUID;

import com.bob.core.application.chat.dto.query.ReadChatMessagesQuery;

public class ReadChatMessagesQueryFixture {

    public static ReadChatMessagesQuery readChatMessagesQuery(UUID memberId) {
        return new ReadChatMessagesQuery(memberId);
    }

    public static ReadChatMessagesQuery readChatMessagesQuery() {
        return readChatMessagesQuery(MEMBER_ID);
    }
}
