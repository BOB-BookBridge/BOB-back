package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;

public interface TradeModifyUseCase {

  void changeTradeStatusProcess(ChangeTradeStatusCommand command);
}
