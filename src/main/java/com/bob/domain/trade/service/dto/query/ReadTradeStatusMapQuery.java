package com.bob.domain.trade.service.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadTradeStatusMapQuery(
    UUID memberId,
    List<Long> postIds
) {

  public static ReadTradeStatusMapQuery of(UUID memberId, List<Long> ids) {
    return new ReadTradeStatusMapQuery(memberId, ids);
  }
}
