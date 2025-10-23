package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.trade.service.port.view.TradeItemView;
import java.util.UUID;

public record TraderSummary(
    UUID id,
    String nickname,
    Item item
) {

  public static TraderSummary of(TradeMemberSummary member, TradeItemView item, int itemSize) {
    return new TraderSummary(member.id(), member.nickname(), Item.of(item.title(), item.cover(), itemSize));
  }

  public record Item(
      String title,
      String cover,
      int size
  ) {

    static Item of(String title, String cover, int size) {
      return new Item(title, cover, size);
    }
  }
}
