package com.bob.domain.trade.service.port.out;

import java.util.UUID;

public interface TradeChatPort {

  Long create(Long postId, Long tradeId, UUID buyerId, boolean isFar);
}
