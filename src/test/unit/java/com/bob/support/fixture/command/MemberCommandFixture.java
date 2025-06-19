package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.defaultIdMember;
import static com.bob.support.fixture.domain.MemberFixture.defaultMember;

import com.bob.domain.member.service.dto.command.ChangePasswordCommand;
import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.IssuePasswordCommand;
import java.util.UUID;

public class MemberCommandFixture {
  public static CreateMemberCommand defaultCreateMemberCommand() {
    return new CreateMemberCommand("test@email.com", "password", "tester", 213);
  }

  public static IssuePasswordCommand defaultIssuePasswordCommand() {
    return new IssuePasswordCommand("test@email.com");
  }

  public static ChangePasswordCommand defaultChangePasswordCommand(String newPassword) {
    return new ChangePasswordCommand(defaultIdMember().getId(), defaultMember().getPassword(), newPassword);
  }

  public static ChangePasswordCommand customChangePasswordCommand(UUID id, String oldPassword, String newPassword) {
    return new ChangePasswordCommand(id, oldPassword, newPassword);
  }
}
