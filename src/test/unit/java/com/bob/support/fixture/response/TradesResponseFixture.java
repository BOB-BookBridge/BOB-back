package com.bob.support.fixture.response;

import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary.Buyer;
import java.util.List;

public class TradesResponseFixture {

  public static TradeSummary FIRST_TRADE = TradeSummary.builder()
      .tradeId(1L)
      .tradeStatus("REQUESTED")
      .buyer(Buyer.of("tester1", "/profile/test.png"))
      .build();

  public static TradeSummary SECOND_TRADE = TradeSummary.builder()
      .tradeId(2L)
      .tradeStatus("REQUESTED")
      .buyer(Buyer.of("tester2", "/profile/test.png"))
      .build();

  public static TradeSummary THIRD_TRADE = TradeSummary.builder()
      .tradeId(3L)
      .tradeStatus("REQUESTED")
      .buyer(Buyer.of("tester3", "/profile/test.png"))
      .build();

  public static TradesResponse DEFAULT_TRADES_RESPONSE() {
    return new TradesResponse(List.of(FIRST_TRADE, SECOND_TRADE, THIRD_TRADE));
  }
}
