package com.bob.support.fixture.chat.dto.command;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import java.util.List;
import java.util.UUID;

import com.bob.core.application.chat.dto.command.CreateMessageCommand;
import com.bob.core.application.chat.dto.command.CreateSystemMessageCommand;

public class ChatMessageCommandFixture {

    public static CreateMessageCommand createMessageCommand(UUID memberId, String content, List<String> fileNames) {
        return new CreateMessageCommand(memberId, content, fileNames);
    }

    public static CreateMessageCommand createMessageCommand() {
        return createMessageCommand(MEMBER_ID, "테스트 메시지", null);
    }

    public static CreateSystemMessageCommand createSystemMessageCommand(String body) {
        return new CreateSystemMessageCommand("TRADE", "1", MEMBER_ID, OTHER_MEMBER_ID, body);
    }
}
