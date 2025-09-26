package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;

public interface MemberBookModifyUseCase {

  void changeMemberBookUsageProcess(ChangeMemberBookUsageCommand command);
}
