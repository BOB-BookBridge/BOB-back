package com.bob.domain.trade.service.dto.command;

import com.bob.domain.trade.entity.type.Owner;
import java.util.List;

public record CreateTradeItemsCommand(
    Long tradeId,
    List<Long> itemIds,
    Owner owner
) {

  public static CreateTradeItemsCommand of(Long tradeId, List<Long> itemIds, Owner owner) {
    return new CreateTradeItemsCommand(tradeId, itemIds, owner);
  }
}
