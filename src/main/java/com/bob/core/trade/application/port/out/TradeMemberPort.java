package com.bob.core.trade.application.port.out;

import java.util.List;
import java.util.UUID;

import com.bob.core.trade.application.port.result.TradeMember;

public interface TradeMemberPort {

    TradeMember readTradeMemberProfile(UUID memberId);

    boolean wishAllMatch(UUID memberId, List<Long> ids);
}
