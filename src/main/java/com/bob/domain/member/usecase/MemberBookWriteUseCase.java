package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;

public interface MemberBookWriteUseCase {

  Long registerMemberBookProcess(RegisterMemberBookCommand command);
}
