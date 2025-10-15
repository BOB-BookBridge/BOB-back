package com.bob.domain.trade.service.port.view;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TradeItemView(
    Long id,
    String status,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static TradeItemView of(Long itemId, String status, String title, String author, Integer priceStandard, String cover, LocalDate pubDate) {
    return TradeItemView.builder()
        .id(itemId)
        .status(status)
        .title(title)
        .author(author)
        .priceStandard(priceStandard)
        .cover(cover)
        .pubDate(pubDate)
        .build();
  }
}