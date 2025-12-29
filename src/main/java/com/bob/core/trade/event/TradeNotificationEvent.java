package com.bob.core.trade.event;

import java.util.UUID;

public record TradeNotificationEvent(Long postId, UUID senderId, UUID receiverId, String body) {

}
