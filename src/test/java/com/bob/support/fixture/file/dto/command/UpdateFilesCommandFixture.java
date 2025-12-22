package com.bob.support.fixture.file.dto.command;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.util.List;

import com.bob.core.file.application.dto.command.UpdateFilesCommand;

public class UpdateFilesCommandFixture {

    public static UpdateFilesCommand createUpdateFilesCommand(String refId) {
        return new UpdateFilesCommand("POST", refId, List.of("post/other1.png", "post/other2.jpg"), MEMBER_ID);
    }
}
