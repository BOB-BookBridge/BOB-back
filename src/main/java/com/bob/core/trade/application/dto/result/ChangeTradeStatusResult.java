package com.bob.core.trade.application.dto.result;

import com.bob.core.trade.domain.Trade;

public record ChangeTradeStatusResult(Trade trade, Long chatroomId) {

}
