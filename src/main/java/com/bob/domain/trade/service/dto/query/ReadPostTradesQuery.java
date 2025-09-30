package com.bob.domain.trade.service.dto.query;

import java.util.UUID;

public record ReadPostTradesQuery(
    Long postId,
    UUID memberId
) {

  public static ReadPostTradesQuery of(Long postId, UUID memberId) {
    return new ReadPostTradesQuery(postId, memberId);
  }
}
