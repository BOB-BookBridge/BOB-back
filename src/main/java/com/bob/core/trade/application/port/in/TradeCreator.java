package com.bob.core.trade.application.port.in;

import com.bob.core.trade.application.dto.command.CreateTradeCommand;
import com.bob.core.trade.domain.Trade;

public interface TradeCreator {

    Trade create(CreateTradeCommand command);
}
