package com.bob.domain.trade.service.port.out;

import java.util.UUID;

public interface TradePostPort {

  UUID readTradePostOwnerId(Long postId);

  void changePostStatus(Long postId, String status);
}
