package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;

public interface TradeWriteUseCase {

  CreateTradeResponse createTradeProcess(CreateTradeCommand command);
}
