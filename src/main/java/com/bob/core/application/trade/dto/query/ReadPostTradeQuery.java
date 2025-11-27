package com.bob.core.application.trade.dto.query;

import java.util.UUID;

public record ReadPostTradeQuery(Long postId, UUID memberId) {

}
