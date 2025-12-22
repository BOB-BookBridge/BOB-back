package com.bob.core.bookcase.application.port.result;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record BookcaseItemResult(
    Long id,
    String isbn,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

}
