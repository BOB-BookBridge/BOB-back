package com.bob.core.trade.event;

public record TradeStatusChangedEvent(Long tradeId, Long postId, String newStatus) {

}
