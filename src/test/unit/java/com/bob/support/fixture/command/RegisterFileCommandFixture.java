package com.bob.support.fixture.command;

import com.bob.domain.file.service.dto.command.RegisterFileCommand;
import java.util.List;

public class RegisterFileCommandFixture {

  public static RegisterFileCommand defaultRegisterFileCommand() {
    return RegisterFileCommand.builder()
        .domain("POST")
        .fileNames(List.of(
            "post/1.jpg",
            "post/2.jpg"
        ))
        .build();
  }
}
