package com.bob.support.fixture.member.dto.command;

import com.bob.core.member.application.dto.command.CreateMemberCommand;

public class CreateMemberCommandFixture {

    public static CreateMemberCommand createCreateMemberCommand() {
        return new CreateMemberCommand("test@email.com", "password", "tester", 213);
    }
}
