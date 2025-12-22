package com.bob.core.trade.application.dto.result;

import lombok.Builder;

import com.bob.core.trade.application.dto.result.internal.TradeMemberDetail;
import com.bob.core.trade.domain.status.Status;

@Builder
public record TradeSummary(Long id, String status, TradeMemberDetail seller, TradeMemberDetail buyer) {

    public static TradeSummary of(Long id, Status status, TradeMemberDetail seller, TradeMemberDetail buyer) {
        return TradeSummary.builder()
            .id(id)
            .status(status.name())
            .seller(seller)
            .buyer(buyer)
            .build();
    }
}
