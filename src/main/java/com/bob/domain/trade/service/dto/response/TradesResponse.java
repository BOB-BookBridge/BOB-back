package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import java.util.List;

public record TradesResponse(
    Long totalCount,
    List<TradeSummary> trades

) {

  public static TradesResponse from(Long size, List<TradeSummary> tradeSummary) {
    return new TradesResponse(size, tradeSummary);
  }
}
