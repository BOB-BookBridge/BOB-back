package com.bob.core.adapter.bookcase.api.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

import com.bob.core.application.bookcase.dto.result.BookcaseItemDetail;

@Builder
public record BookcaseItemDetailResponse(
    Long id,
    String status,
    String isbn,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate,
    boolean available
) {

    public static BookcaseItemDetailResponse of(BookcaseItemDetail detail) {
        return BookcaseItemDetailResponse.builder()
            .id(detail.id())
            .status(detail.status())
            .isbn(detail.isbn())
            .title(detail.title())
            .author(detail.author())
            .priceStandard(detail.priceStandard())
            .cover(detail.cover())
            .pubDate(detail.pubDate())
            .available(detail.available())
            .build();
    }

    public static List<BookcaseItemDetailResponse> listOf(List<BookcaseItemDetail> details) {
        return details.stream()
            .map(BookcaseItemDetailResponse::of)
            .toList();
    }
}
