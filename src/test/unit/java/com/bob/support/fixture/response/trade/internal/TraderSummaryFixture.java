package com.bob.support.fixture.response.trade.internal;

import static com.bob.support.fixture.response.trade.internal.TradeItemViewFixture.DEFAULT_BUYER_ITEM_VIEW;
import static com.bob.support.fixture.response.trade.internal.TradeItemViewFixture.DEFAULT_SELLER_ITEM_VIEW;
import static com.bob.support.fixture.response.trade.internal.TradeMemberSummaryFixture.DEFAULT_TRADE_BUYER_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradeMemberSummaryFixture.DEFAULT_TRADE_SELLER_SUMMARY;

import com.bob.domain.trade.service.dto.response.internal.TraderSummary;

public class TraderSummaryFixture {

  public static TraderSummary DEFAULT_TRADER_SUMMARY = TraderSummary.of(
      DEFAULT_TRADE_SELLER_SUMMARY,
      DEFAULT_SELLER_ITEM_VIEW,
      1
  );

  public static TraderSummary OTHER_TRADER_SUMMARY = TraderSummary.of(
      DEFAULT_TRADE_BUYER_SUMMARY,
      DEFAULT_BUYER_ITEM_VIEW,
      2
  );
}
