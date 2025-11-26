package com.bob.core.application.trade.dto.result;

import com.bob.core.domain.trade.Trade;

public record ChangeTradeStatusResult(Trade trade, Long chatroomId) {

}
