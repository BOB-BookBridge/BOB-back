package com.bob.domain.trade.service.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateTradeCommand(
    Long postId,
    UUID buyerId,
    List<Long> itemIds
) {

  public static CreateTradeCommand of(Long postId, UUID buyerId, List<Long> itemIds) {
    return new CreateTradeCommand(postId, buyerId, itemIds);
  }
}
