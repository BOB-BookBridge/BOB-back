package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.service.dto.response.internal.TradeItemSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary;
import com.bob.domain.trade.service.dto.response.internal.TradePostSummary;
import java.util.List;
import java.util.UUID;

public record TradeDetailResponse(
    Long id,
    String status,
    Post post,
    Trader seller,
    Trader buyer
) {

  public static TradeDetailResponse from(Long id, String status, Post post, Trader seller, Trader buyer) {
    return new TradeDetailResponse(id, status, post, seller, buyer);
  }

  public record Post(
      Long id,
      String title,
      String cover
  ) {
    public static Post from(TradePostSummary post) {
      return new Post(post.id(), post.title(), post.thumbnailUrl());
    }
  }

  public record Trader(
      UUID id,
      String nickname,
      Integer worth,
      List<TradeItemSummary> item
  ) {
    public static Trader from(TradeMemberSummary trader, int itemsWorth, List<TradeItemSummary> items) {
      return new Trader(trader.id(), trader.nickname(), itemsWorth, items);
    }
  }
}
