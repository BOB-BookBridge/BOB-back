package com.bob.core.trade.application.dto.result.internal;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.Builder;

import com.bob.core.trade.application.port.result.TradeBookcaseItem;

@Builder
public record TradeItemSummary(
    Long id,
    String status,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate,
    boolean available
) {

    public static List<TradeItemSummary> listFrom(List<TradeBookcaseItem> tradeItems, List<Long> itemIds) {
        return tradeItems.stream()
            .filter(Objects::nonNull)
            .filter(it -> itemIds == null || itemIds.isEmpty() || itemIds.contains(it.id()))
            .map(TradeItemSummary::from)
            .toList();
    }

    public static TradeItemSummary from(TradeBookcaseItem tradeItem) {
        return TradeItemSummary.builder()
            .id(tradeItem.id())
            .status(tradeItem.status())
            .title(tradeItem.title())
            .author(tradeItem.author())
            .priceStandard(tradeItem.priceStandard())
            .cover(tradeItem.cover())
            .pubDate(tradeItem.pubDate())
            .available(tradeItem.available())
            .build();
    }
}
