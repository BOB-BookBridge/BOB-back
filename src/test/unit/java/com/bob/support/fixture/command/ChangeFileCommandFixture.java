package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.file.service.dto.command.ChangeFileCommand;
import java.util.List;

public class ChangeFileCommandFixture {

  public static final ChangeFileCommand DEFAULT_CHANGE_FILE_COMMAND_REF_ID_1 = ChangeFileCommand.builder()
      .domain("POST")
      .referenceId("1")
      .fileNames(List.of("post/other1.png", "post/other2.jpg"))
      .memberId(MEMBER_ID)
      .build();

  public static final ChangeFileCommand DEFAULT_CHANGE_FILE_COMMAND_REF_ID_2 = ChangeFileCommand.builder()
      .domain("POST")
      .referenceId("2")
      .fileNames(List.of("post/other1.png", "post/other2.jpg"))
      .memberId(MEMBER_ID)
      .build();
}
