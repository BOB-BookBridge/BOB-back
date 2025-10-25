package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;

public interface MemberWishWriteUseCase {

  void createMemberWishProcess(CreateMemberWishCommand command);
}
