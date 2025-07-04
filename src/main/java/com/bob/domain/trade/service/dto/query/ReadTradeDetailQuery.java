package com.bob.domain.trade.service.dto.query;

import java.util.UUID;

public record ReadTradeDetailQuery(
    Long tradeId,
    UUID memberId
) {

  public static ReadTradeDetailQuery of(Long tradeId, UUID memberId) {
    return new ReadTradeDetailQuery(tradeId, memberId);
  }
}
