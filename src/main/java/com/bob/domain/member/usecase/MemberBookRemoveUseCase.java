package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;

public interface MemberBookRemoveUseCase {

  void removeMemberBookProcess(RemoveMemberBookCommand command);

  void removeMemberBooksProcess(RemoveMemberBooksCommand command);
}
