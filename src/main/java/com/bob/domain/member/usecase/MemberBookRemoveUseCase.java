package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;

public interface MemberBookRemoveUseCase {

  void removeMemberBookProcess(RemoveMemberBookCommand command);
}
