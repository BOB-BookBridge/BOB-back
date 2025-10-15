package com.bob.support.fixture.response.trade.internal;

import com.bob.domain.trade.service.dto.response.internal.TradeItemSummary;
import java.time.LocalDate;

public class TradeItemSummaryFixture {

  public static TradeItemSummary DEFAULT_SELLER_TRADE_ITEM_SUMMARY = TradeItemSummary.builder()
      .id(1L)
      .status("BEST")
      .title("제목")
      .author("작가")
      .priceStandard(10000)
      .cover("http://cover.png")
      .pubDate(LocalDate.now())
      .build();

  public static TradeItemSummary DEFAULT_BUYER_TRADE_ITEM_SUMMARY = TradeItemSummary.builder()
      .id(2L)
      .status("BEST")
      .title("제목")
      .author("작가")
      .priceStandard(5000)
      .cover("http://cover.png")
      .pubDate(LocalDate.now())
      .build();

  public static TradeItemSummary CUSTOM_TRADE_ITEM_SUMMARY(Long id, int price) {
    return TradeItemSummary.builder()
        .id(id)
        .status("HIGH")
        .title("제목")
        .author("작가")
        .priceStandard(price)
        .cover("http://cover.png")
        .pubDate(LocalDate.now())
        .build();
  }
}
