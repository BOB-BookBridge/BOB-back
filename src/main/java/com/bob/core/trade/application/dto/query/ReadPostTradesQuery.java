package com.bob.core.trade.application.dto.query;

import java.util.UUID;

public record ReadPostTradesQuery(Long postId, UUID memberId) {

}
