package com.bob.support.fixture.command;

import com.bob.domain.member.service.dto.command.ChangeProfileCommand;
import java.util.List;
import java.util.UUID;

public class ChangeProfileCommandFixture {

  public static ChangeProfileCommand sameChangeProfileCommand(UUID memberId) {
    return new ChangeProfileCommand(memberId, "tester", List.of("interest1", "interest2"));
  }
}
