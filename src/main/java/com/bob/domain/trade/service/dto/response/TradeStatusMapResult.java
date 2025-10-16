package com.bob.domain.trade.service.dto.response;

import java.util.Map;

public record TradeStatusMapResult(
    Map<Long, String> statusMap
) {

  public static TradeStatusMapResult of(Map<Long, String> map) {
    return new TradeStatusMapResult(map);
  }
}
