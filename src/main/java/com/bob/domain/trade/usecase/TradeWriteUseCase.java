package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;

public interface TradeWriteUseCase {

  Long createTradeProcess(CreateTradeCommand command);
}
