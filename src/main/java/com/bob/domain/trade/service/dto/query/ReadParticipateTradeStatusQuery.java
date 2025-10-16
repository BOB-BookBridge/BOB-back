package com.bob.domain.trade.service.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadParticipateTradeStatusQuery(
    UUID memberId,
    List<Long> postIds
) {

  public static ReadParticipateTradeStatusQuery of(UUID memberId, List<Long> ids) {
    return new ReadParticipateTradeStatusQuery(memberId, ids);
  }
}
