package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import java.util.List;

public record TradesResponse(
    List<TradeSummary> trades
) {

  public static TradesResponse of(List<TradeSummary> trades) {
    return new TradesResponse(trades);
  }
}
