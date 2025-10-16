package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.trade.service.port.view.TradeItemView;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record TradeItemSummary(
    Long id,
    String status,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static TradeItemSummary from(TradeItemView tradeItem) {
    return TradeItemSummary.builder()
        .id(tradeItem.id())
        .status(tradeItem.status())
        .title(tradeItem.title())
        .author(tradeItem.author())
        .priceStandard(tradeItem.priceStandard())
        .cover(tradeItem.cover())
        .pubDate(tradeItem.pubDate())
        .build();
  }

  public static List<TradeItemSummary> listFrom(List<TradeItemView> tradeItems, List<Long> itemIds) {
    return tradeItems.stream()
        .filter(Objects::nonNull)
        .filter(it -> itemIds == null || itemIds.isEmpty() || itemIds.contains(it.id()))
        .map(TradeItemSummary::from)
        .toList();
  }

  public static List<TradeItemSummary> listFrom(List<TradeItemView> tradeItems) {
    return listFrom(tradeItems, null);
  }
}