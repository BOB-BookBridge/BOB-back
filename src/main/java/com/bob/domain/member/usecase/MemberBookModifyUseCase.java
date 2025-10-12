package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookUsageCommand;

public interface MemberBookModifyUseCase {

  void changeMemberBookUsageProcess(ChangeMemberBookUsageCommand command);

  void removeMemberBookUsageProcess(RemoveMemberBookUsageCommand command);
}
