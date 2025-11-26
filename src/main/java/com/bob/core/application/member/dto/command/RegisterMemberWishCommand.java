package com.bob.core.application.member.dto.command;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record RegisterMemberWishCommand(
    String isbn,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

    public static RegisterMemberWishCommand of(
        String isbn,
        String title,
        String author,
        String description,
        int priceStandard,
        String cover,
        LocalDate pubDate
    ) {
        return RegisterMemberWishCommand.builder()
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
