package com.bob.support.fixture.chat.dto.command;

import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import java.util.UUID;

import com.bob.core.chat.application.dto.command.CreateChatroomCommand;

public class CreateChatroomCommandFixture {

    public static CreateChatroomCommand createChatroomCommand(Long postId, Long tradeId, UUID buyerId, boolean isFar) {
        return new CreateChatroomCommand(postId, tradeId, buyerId, isFar);
    }

    public static CreateChatroomCommand createChatroomCommand() {
        return createChatroomCommand(1L, 1L, OTHER_MEMBER_ID, false);
    }
}
