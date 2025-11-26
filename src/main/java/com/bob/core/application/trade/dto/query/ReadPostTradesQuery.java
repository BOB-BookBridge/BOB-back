package com.bob.core.application.trade.dto.query;

import java.util.UUID;

public record ReadPostTradesQuery(Long postId, UUID memberId) {

}
