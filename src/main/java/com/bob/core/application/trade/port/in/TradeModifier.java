package com.bob.core.application.trade.port.in;

import com.bob.core.application.trade.dto.command.ChangeTradeItemsCommand;
import com.bob.core.application.trade.dto.command.ChangeTradeStatusCommand;
import com.bob.core.application.trade.dto.result.ChangeTradeStatusResult;
import com.bob.core.domain.trade.Trade;

public interface TradeModifier {

    Trade changeItems(Long id, ChangeTradeItemsCommand command);

    ChangeTradeStatusResult changeStatus(Long id, ChangeTradeStatusCommand command);
}
