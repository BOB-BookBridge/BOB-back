package com.bob.core.application.trade.port.out;

import java.util.List;
import java.util.UUID;

public interface TradeMemberWishPort {

    boolean allMatch(UUID memberId, List<Long> ids);
}
