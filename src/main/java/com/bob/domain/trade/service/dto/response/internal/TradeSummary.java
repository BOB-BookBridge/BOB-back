package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.trade.entity.status.Status;
import lombok.Builder;

@Builder
public record TradeSummary(
    Long id,
    String status,
    TradePostSummary post
) {

  public static TradeSummary of(Long id, Status status, TradePostSummary post) {
    return TradeSummary.builder()
        .id(id)
        .status(status.name())
        .post(post)
        .build();
  }
}
