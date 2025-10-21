package com.bob.web.member.adapter.in;

import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.trade.service.port.out.TradeMemberBookPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TradeMemberBookAdapter implements TradeMemberBookPort {

  private final MemberBookModifyUseCase modifyUseCase;
  private final MemberBookRemoveUseCase removeUseCase;

  @Override
  public void allocateUsage(List<Long> ids, Long usageId) {
    AllocateMemberBookUsageCommand command = AllocateMemberBookUsageCommand.of(ids, usageId);
    modifyUseCase.allocateMemberBookUsageProcess(command);
  }

  @Override
  public void freeUsage(List<Long> ids) {
    FreeMemberBookUsageByIdsCommand command = FreeMemberBookUsageByIdsCommand.of(ids);
    modifyUseCase.freeMemberBookUsageByIdsProcess(command);
  }

  @Override
  public void remove(List<Long> memberBookIds) {
    RemoveMemberBooksCommand command = RemoveMemberBooksCommand.of(memberBookIds);
    removeUseCase.removeMemberBooksProcess(command);
  }
}
