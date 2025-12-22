package com.bob.core.trade.application.dto.query;

import java.util.UUID;

public record ReadPostTradeQuery(Long postId, UUID memberId) {

}
