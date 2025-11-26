package com.bob.core.application.trade.port.in;

import com.bob.core.application.trade.dto.command.RemoveTradeCommand;

public interface TradeRemover {

    void remove(Long id, RemoveTradeCommand command);
}
