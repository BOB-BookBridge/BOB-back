package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.DeleteTradeCommand;

public interface TradeDeleteUseCase {

  void deleteTradeProcess(DeleteTradeCommand command);
}
