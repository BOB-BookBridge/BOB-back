package com.bob.domain.chat.service.dto.response;

import com.bob.domain.chat.entity.status.TradeStatus;

public record ChatTradeResponse(
    Long id,
    TradeStatus status
) {

  public static ChatTradeResponse of(Long id, TradeStatus status) {
    return new ChatTradeResponse(id, status);
  }
}
