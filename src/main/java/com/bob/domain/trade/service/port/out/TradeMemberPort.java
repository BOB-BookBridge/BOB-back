package com.bob.domain.trade.service.port.out;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public interface TradeMemberPort {

  MemberProfileResponse readTradeMemberProfile(UUID memberId);
}
