package com.bob.core.trade.application.dto.result.internal;

import com.bob.core.trade.application.port.result.TradeMember;
import com.bob.core.trade.domain.Trade;

public record PostTrade(Long id, String status, TradeMember buyer) {

    public static PostTrade of(Trade trade, TradeMember member) {
        return new PostTrade(trade.getId(), trade.getStatus().name(), member);
    }
}
