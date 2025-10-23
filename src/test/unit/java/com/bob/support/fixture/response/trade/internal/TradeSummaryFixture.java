package com.bob.support.fixture.response.trade.internal;

import static com.bob.support.fixture.response.trade.internal.TraderSummaryFixture.DEFAULT_TRADER_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TraderSummaryFixture.OTHER_TRADER_SUMMARY;

import com.bob.domain.trade.service.dto.response.internal.TradeSummary;

public class TradeSummaryFixture {

  public static final TradeSummary REQUESTED_RECEIVED_TRADE_SUMMARY1 = TradeSummary.builder()
      .id(1L)
      .status("REQUESTED")
      .seller(DEFAULT_TRADER_SUMMARY)
      .buyer(OTHER_TRADER_SUMMARY)
      .build();

  public static final TradeSummary RESERVED_RECEIVED_TRADE_SUMMARY2 = TradeSummary.builder()
      .id(2L)
      .status("RESERVED")
      .seller(DEFAULT_TRADER_SUMMARY)
      .buyer(OTHER_TRADER_SUMMARY)
      .build();
}
