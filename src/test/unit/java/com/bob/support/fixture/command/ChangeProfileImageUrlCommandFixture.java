package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.command.ChangeProfileImageCommand;
import java.util.UUID;

public class ChangeProfileImageUrlCommandFixture {

  public static ChangeProfileImageCommand defaultChangeProfileImageUrlCommand() {
    return new ChangeProfileImageCommand(MEMBER_ID, "profile/temp-uuid");
  }

  public static ChangeProfileImageCommand customChangeProfileImageUrlCommand(UUID memberId) {
    return new ChangeProfileImageCommand(memberId, "profile/temp-uuid");
  }
}
