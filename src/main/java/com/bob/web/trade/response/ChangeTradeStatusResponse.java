package com.bob.web.trade.response;

public record ChangeTradeStatusResponse(
    Long chatroomId
) {

  public static ChangeTradeStatusResponse of(Long chatroomId) {
    return new ChangeTradeStatusResponse(chatroomId);
  }
}
