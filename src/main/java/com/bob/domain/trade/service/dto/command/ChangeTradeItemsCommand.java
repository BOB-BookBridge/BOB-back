package com.bob.domain.trade.service.dto.command;

import java.util.List;
import java.util.UUID;

public record ChangeTradeItemsCommand(
    Long tradeId,
    List<Long> itemIds,
    UUID memberId
) {

  public static ChangeTradeItemsCommand of(Long tradeId, List<Long> itemIds, UUID memberId) {
    return new ChangeTradeItemsCommand(tradeId, itemIds, memberId);
  }
}
