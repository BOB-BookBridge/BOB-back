package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.CreateMemberCommand;

public interface MemberWriteUseCase {

  void signupProcess(CreateMemberCommand command);
}
