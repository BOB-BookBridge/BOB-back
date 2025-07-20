package com.bob.domain.trade.service.dto.query;

import java.util.UUID;

public record ReadTradesQuery(
    Long postId,
    UUID memberId
) {

}
