package com.bob.core.application.trade.dto.result.internal;

import com.bob.core.application.trade.port.result.TradeMember;
import com.bob.core.domain.trade.Trade;

public record PostTrade(Long id, String status, TradeMember buyer) {

    public static PostTrade of(Trade trade, TradeMember member) {
        return new PostTrade(trade.getId(), trade.getStatus().name(), member);
    }
}
