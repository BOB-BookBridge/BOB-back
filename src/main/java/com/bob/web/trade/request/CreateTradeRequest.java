package com.bob.web.trade.request;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import java.util.List;
import java.util.UUID;

public record CreateTradeRequest(
    Long postId,
    List<Long> exchangeBookIds,
    boolean isFar
) {

  public CreateTradeCommand toCommand(UUID memberId) {
    return CreateTradeCommand.of(postId, memberId, exchangeBookIds);
  }
}
