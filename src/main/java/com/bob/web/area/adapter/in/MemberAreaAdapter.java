package com.bob.web.area.adapter.in;

import com.bob.domain.area.service.dto.command.CreateAreaCommand;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.area.usecase.AreaReadUseCase;
import com.bob.domain.area.usecase.AreaWriteUseCase;
import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import com.bob.domain.member.service.port.out.MemberAreaPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberAreaAdapter implements MemberAreaPort {

  private final AreaWriteUseCase writeUseCase;
  private final AreaReadUseCase readUseCase;

  @Override
  public void createMemberActivityArea(UUID memberId, Integer emdId) {
    writeUseCase.createActivityAreaProcess(CreateAreaCommand.of(memberId, emdId));
  }

  @Override
  public MemberAreaSummaryResponse readMemberAreaSummary(UUID memberId) {
    AreaSummaryResponse response = readUseCase.readAreaSummaryProcess(ReadAreaQuery.of(memberId));
    return MemberAreaSummaryResponse.of(response.emdId(), response.validity(), response.authenticatedAt());
  }
}
