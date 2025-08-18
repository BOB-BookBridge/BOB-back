package com.bob.domain.member.service.port.out;

import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import java.util.UUID;

public interface MemberAreaPort {

  void createMemberActivityArea(UUID memberId, Integer emdId);

  void createNonAuthenticatedActivityArea(UUID memberId, Integer emdId);

  MemberAreaSummaryResponse readMemberAreaSummary(UUID memberId);
}
