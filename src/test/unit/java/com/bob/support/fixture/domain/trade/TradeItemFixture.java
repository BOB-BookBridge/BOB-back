package com.bob.support.fixture.domain.trade;

import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.entity.type.Owner;

public class TradeItemFixture {

  public static final TradeItem DEFAULT_TRADE_SELLER_ITEM_1 = TradeItem.builder()
      .id(1L)
      .tradeId(1L)
      .itemId(1L)
      .owner(Owner.SELLER)
      .build();

  public static final TradeItem DEFAULT_TRADE_SELLER_ITEM_2 = TradeItem.builder()
      .id(2L)
      .tradeId(1L)
      .itemId(2L)
      .owner(Owner.SELLER)
      .build();

  public static final TradeItem DEFAULT_TRADE_BUYER_ITEM_1 = TradeItem.builder()
      .id(3L)
      .tradeId(1L)
      .itemId(3L)
      .owner(Owner.BUYER)
      .build();

  public static final TradeItem DEFAULT_TRADE_BUYER_ITEM_2 = TradeItem.builder()
      .id(4L)
      .tradeId(1L)
      .itemId(4L)
      .owner(Owner.BUYER)
      .build();
}
