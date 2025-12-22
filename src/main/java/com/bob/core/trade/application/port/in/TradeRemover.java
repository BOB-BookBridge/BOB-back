package com.bob.core.trade.application.port.in;

import com.bob.core.trade.application.dto.command.RemoveTradeCommand;

public interface TradeRemover {

    void remove(Long id, RemoveTradeCommand command);
}
