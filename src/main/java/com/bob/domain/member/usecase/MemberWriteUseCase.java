package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.CreateMemberCommand;
import com.bob.domain.member.service.dto.command.SocialLoginCommand;
import java.util.UUID;

public interface MemberWriteUseCase {

  void signupProcess(CreateMemberCommand command);

  UUID socialLoginProcess(SocialLoginCommand command);
}
