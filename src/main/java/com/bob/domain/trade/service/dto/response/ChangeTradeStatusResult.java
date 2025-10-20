package com.bob.domain.trade.service.dto.response;

public record ChangeTradeStatusResult(
    Long chatroomId
) {

  public static ChangeTradeStatusResult of(Long chatroomId) {
    return new ChangeTradeStatusResult(chatroomId);
  }
}
