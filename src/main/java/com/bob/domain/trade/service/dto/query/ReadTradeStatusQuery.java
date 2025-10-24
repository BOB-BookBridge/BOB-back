package com.bob.domain.trade.service.dto.query;

public record ReadTradeStatusQuery(
    Long id
) {

  public static ReadTradeStatusQuery of(Long id) {
    return new ReadTradeStatusQuery(id);
  }
}
