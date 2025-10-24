package com.bob.domain.trade.service.dto.response;

public record TradeStatusResult(
    String status
) {

  public static TradeStatusResult of(String status) {
    return new TradeStatusResult(status);
  }
}
