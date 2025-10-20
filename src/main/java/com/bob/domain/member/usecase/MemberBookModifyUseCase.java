package com.bob.domain.member.usecase;

import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByUsageIdCommand;

public interface MemberBookModifyUseCase {

  // TODO : 할당, 해제 역할 분리
  void changeMemberBookUsageProcess(ChangeMemberBookUsageCommand command);

  void allocateMemberBookUsageProcess(AllocateMemberBookUsageCommand command);

  void freeMemberBookUsageByIdsProcess(FreeMemberBookUsageByIdsCommand command);

  void freeMemberBookUsageByUsageIdProcess(FreeMemberBookUsageByUsageIdCommand command);
}
