package com.bob.domain.trade.service.dto.command;

import java.util.UUID;

public record DeleteTradeCommand(
    Long id,
    UUID requesterId
) {

  public static DeleteTradeCommand of(Long id, UUID requesterId) {
    return new DeleteTradeCommand(id, requesterId);
  }
}
