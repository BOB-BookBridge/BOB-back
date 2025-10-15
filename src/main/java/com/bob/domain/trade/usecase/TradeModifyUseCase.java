package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;

public interface TradeModifyUseCase {

  void changeTradeStatusProcess(ChangeTradeStatusCommand command);

  void changeTradeItemProcess(ChangeTradeItemsCommand command);
}
