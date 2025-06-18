package com.bob.domain.area.adapter;

import com.bob.domain.area.service.AreaService;
import com.bob.domain.area.service.dto.command.CreateAreaCommand;
import com.bob.domain.area.service.dto.query.ReadAreaQuery;
import com.bob.domain.area.service.dto.response.AreaSummaryResponse;
import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import com.bob.domain.member.service.port.MemberAreaPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberAreaAdapter implements MemberAreaPort {

  private final AreaService areaService;

  @Override
  public void createMemberActivityArea(UUID memberId, Integer emdId) {
    areaService.createActivityAreaProcess(CreateAreaCommand.of(memberId, emdId));
  }

  @Override
  public MemberAreaSummaryResponse readMemberAreaSummary(UUID memberId) {
    AreaSummaryResponse response = areaService.readAreaSummaryProcess(ReadAreaQuery.of(memberId));
    return MemberAreaSummaryResponse.of(response.emdId(), response.validity(), response.authenticatedAt());
  }
}
