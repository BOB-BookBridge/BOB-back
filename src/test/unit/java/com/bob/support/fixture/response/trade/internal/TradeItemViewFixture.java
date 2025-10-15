package com.bob.support.fixture.response.trade.internal;

import com.bob.domain.trade.service.port.view.TradeItemView;
import java.time.LocalDate;
import java.util.List;

public class TradeItemViewFixture {

  public static TradeItemView DEFAULT_SELLER_ITEM_VIEW = TradeItemView.builder()
      .id(1L)
      .status("BEST")
      .title("제목1")
      .author("작가1")
      .priceStandard(10000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static TradeItemView DEFAULT_BUYER_ITEM_VIEW = TradeItemView.builder()
      .id(2L)
      .status("BEST")
      .title("제목2")
      .author("작가2")
      .priceStandard(6000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static TradeItemView SECOND_BUYER_ITEM_VIEW = TradeItemView.builder()
      .id(3L)
      .status("LOW")
      .title("제목3")
      .author("작가3")
      .priceStandard(8000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static List<TradeItemView> ALL_TRADE_ITEM_VIEWS = List.of(
      DEFAULT_SELLER_ITEM_VIEW,
      DEFAULT_BUYER_ITEM_VIEW,
      SECOND_BUYER_ITEM_VIEW
  );
}
