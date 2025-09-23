package com.bob.domain.post.service.dto.response.internal;

import com.bob.domain.book.service.dto.response.BookResponse;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PostBookSummaryResponse(
    Long id,
    String isbn13,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static PostBookSummaryResponse from(BookResponse response) {
    return PostBookSummaryResponse.builder()
        .id(response.id())
        .isbn13(response.isbn13())
        .title(response.title())
        .author(response.author())
        .description(response.description())
        .priceStandard(response.priceStandard())
        .cover(response.cover())
        .pubDate(response.pubDate())
        .build();
  }
}
