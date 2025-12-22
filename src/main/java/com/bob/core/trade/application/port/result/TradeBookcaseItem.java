package com.bob.core.trade.application.port.result;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record TradeBookcaseItem(
    Long id, Long bookId, String status, String title, String author,
    Integer priceStandard, String cover, LocalDate pubDate, boolean available
) {

}
