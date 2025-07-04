package com.bob.domain.chat.service.dto.response;

import com.bob.domain.trade.service.dto.response.TradeDetailResponse;

public record ChatTradeResponse(
    Long id,
    String status
) {

  public static ChatTradeResponse from(TradeDetailResponse response) {
    return new ChatTradeResponse(response.id(), response.status());
  }
}
