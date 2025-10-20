package com.bob.domain.trade.usecase;

import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;

public interface TradeModifyUseCase {

  ChangeTradeStatusResult changeTradeStatusProcess(ChangeTradeStatusCommand command);

  void changeTradeItemProcess(ChangeTradeItemsCommand command);
}
