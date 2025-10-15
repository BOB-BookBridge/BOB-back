package com.bob.support.fixture.response.trade;

import static com.bob.support.fixture.response.trade.internal.TradeItemSummaryFixture.CUSTOM_TRADE_ITEM_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradeItemSummaryFixture.DEFAULT_BUYER_TRADE_ITEM_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradeItemSummaryFixture.DEFAULT_SELLER_TRADE_ITEM_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradeMemberSummaryFixture.DEFAULT_TRADE_BUYER_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradeMemberSummaryFixture.DEFAULT_TRADE_SELLER_SUMMARY;
import static com.bob.support.fixture.response.trade.internal.TradePostSummaryFixture.DEFAULT_TRADE_POST_SUMMARY;

import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.internal.TradeItemSummary;
import java.util.List;

public final class TradeDetailResponseFixture {

  public static TradeDetailResponse DEFAULT_TRADE_DETAIL_RESPONSE = of(1L, "REQUESTED");

  public static TradeDetailResponse of(Long tradeId, String status) {
    List<TradeItemSummary> sellerItems = List.of(DEFAULT_SELLER_TRADE_ITEM_SUMMARY);
    List<TradeItemSummary> buyerItems = List.of(DEFAULT_BUYER_TRADE_ITEM_SUMMARY, CUSTOM_TRADE_ITEM_SUMMARY(3L, 500));

    int sellerWorth = sellerItems.stream().map(TradeItemSummary::priceStandard).mapToInt(Integer::intValue).sum();
    int buyerWorth = buyerItems.stream().map(TradeItemSummary::priceStandard).mapToInt(Integer::intValue).sum();

    return TradeDetailResponse.from(tradeId, status,
        TradeDetailResponse.Post.from(DEFAULT_TRADE_POST_SUMMARY),
        TradeDetailResponse.Trader.from(DEFAULT_TRADE_SELLER_SUMMARY, sellerWorth, sellerItems),
        TradeDetailResponse.Trader.from(DEFAULT_TRADE_BUYER_SUMMARY, buyerWorth, buyerItems)
    );
  }
}
