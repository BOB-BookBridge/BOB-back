package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.trade.entity.status.Status;
import lombok.Builder;

@Builder
public record TradeSummary(
    Long id,
    String status,
    TraderSummary seller,
    TraderSummary buyer
) {

  public static TradeSummary of(Long id, Status status, TraderSummary seller, TraderSummary buyer) {
    return TradeSummary.builder()
        .id(id)
        .status(status.name())
        .seller(seller)
        .buyer(buyer)
        .build();
  }
}
