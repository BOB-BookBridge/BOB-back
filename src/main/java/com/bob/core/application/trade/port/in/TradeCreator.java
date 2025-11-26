package com.bob.core.application.trade.port.in;

import com.bob.core.application.trade.dto.command.CreateTradeCommand;
import com.bob.core.domain.trade.Trade;

public interface TradeCreator {

    Trade create(CreateTradeCommand command);
}
