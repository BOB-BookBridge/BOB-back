package com.bob.domain.member.service.port;

import com.bob.domain.member.service.dto.response.MemberAreaSummaryResponse;
import java.util.UUID;

public interface MemberAreaPort {

  void createMemberActivityArea(UUID memberId, Integer emdId);

  MemberAreaSummaryResponse readMemberAreaSummary(UUID memberId);
}
