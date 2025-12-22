package com.bob.core.trade.application.port.result;

import java.util.UUID;

import lombok.Builder;

@Builder
public record TradePost(
    Long id, String status, String tradeStatus, UUID sellerId, Long sellerBookId,
    String title, String thumbnailUrl, boolean wishOnly
) {

}
