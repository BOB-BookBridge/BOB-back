package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import java.util.List;

public record TradesResponse(
    List<TradeSummary> trades,
    Long size
) {

  public static TradesResponse from(List<TradeSummary> tradeSummary, Long size) {
    return new TradesResponse(tradeSummary, size);
  }
}
