package com.bob.support.fixture.response;

import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.internal.PostTradeSummary;
import com.bob.domain.trade.service.dto.response.internal.PostTradeSummary.Buyer;
import java.util.List;
import java.util.UUID;

public class PostTradesResponseFixture {

  public static PostTradeSummary FIRST_TRADE = PostTradeSummary.builder()
      .id(1L)
      .status("REQUESTED")
      .buyer(Buyer.of(UUID.randomUUID(), "tester1", "/profile/test.png"))
      .build();

  public static PostTradeSummary SECOND_TRADE = PostTradeSummary.builder()
      .id(2L)
      .status("REQUESTED")
      .buyer(Buyer.of(UUID.randomUUID(), "tester2", "/profile/test.png"))
      .build();

  public static PostTradeSummary THIRD_TRADE = PostTradeSummary.builder()
      .id(3L)
      .status("REQUESTED")
      .buyer(Buyer.of(UUID.randomUUID(), "tester3", "/profile/test.png"))
      .build();

  public static PostTradesResponse DEFAULT_POST_TRADES_RESPONSE() {
    return new PostTradesResponse(List.of(FIRST_TRADE, SECOND_TRADE, THIRD_TRADE));
  }
}
