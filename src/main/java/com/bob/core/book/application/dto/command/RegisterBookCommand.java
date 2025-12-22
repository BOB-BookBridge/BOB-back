package com.bob.core.book.application.dto.command;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record RegisterBookCommand(
    String isbn, String title, String author,
    String description, Integer priceStandard, String cover, LocalDate pubDate
) {

    public static RegisterBookCommand of(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    ) {
        return RegisterBookCommand.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .description(description)
            .priceStandard(priceStandard)
            .cover(cover)
            .pubDate(pubDate)
            .build();
    }
}
