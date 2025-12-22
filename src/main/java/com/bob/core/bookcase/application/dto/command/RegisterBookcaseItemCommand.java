package com.bob.core.bookcase.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RegisterBookcaseItemCommand(
    UUID memberId, String status, String isbn, String title, String author,
    String description, Integer priceStandard, String cover, LocalDate pubDate
) {

    public static RegisterBookcaseItemCommand of(
        UUID memberId, String status, String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    ) {
        return RegisterBookcaseItemCommand.builder()
            .memberId(memberId)
            .status(status)
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
