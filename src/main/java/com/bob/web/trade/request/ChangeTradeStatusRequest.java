package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import java.util.UUID;

public record ChangeTradeStatusRequest(
    String status
) {

  public ChangeTradeStatusCommand toCommand(UUID memberId, Long tradeId) {
    return ChangeTradeStatusCommand.builder()
        .memberId(memberId)
        .tradeId(tradeId)
        .status(status)
        .build();
  }
}
