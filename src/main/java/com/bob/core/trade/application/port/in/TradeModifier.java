package com.bob.core.trade.application.port.in;

import com.bob.core.trade.application.dto.command.ChangeTradeItemsCommand;
import com.bob.core.trade.application.dto.command.ChangeTradeStatusCommand;
import com.bob.core.trade.application.dto.result.ChangeTradeStatusResult;
import com.bob.core.trade.domain.Trade;

public interface TradeModifier {

    Trade changeItems(Long id, ChangeTradeItemsCommand command);

    ChangeTradeStatusResult changeStatus(Long id, ChangeTradeStatusCommand command);
}
