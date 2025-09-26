package com.bob.domain.trade.service.dto.response;

public record CreateTradeResponse(
    Long id
) {

  public static CreateTradeResponse of(Long id) {
    return new CreateTradeResponse(id);
  }
}
