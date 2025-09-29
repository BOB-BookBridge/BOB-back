package com.bob.domain.trade.service.port.out;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.List;
import java.util.UUID;

public interface TradeMemberPort {

  MemberProfileResponse readTradeMemberProfile(UUID memberId);

  void changeMemberBookUsage(Long usageId, List<Long> memberBookIds);
}
