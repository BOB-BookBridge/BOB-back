package com.bob.core.trade.event;

import java.util.UUID;

public record TradeChangedEvent(Long postId, UUID senderId, UUID receiverId, String body) {

}
