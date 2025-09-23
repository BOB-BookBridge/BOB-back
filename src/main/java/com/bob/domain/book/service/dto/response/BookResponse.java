package com.bob.domain.book.service.dto.response;

import com.bob.domain.book.entity.Book;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookResponse(
    Long id,
    String isbn13,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static BookResponse from(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .isbn13(book.getIsbn13())
        .title(book.getTitle())
        .author(book.getAuthor())
        .description(book.getDescription())
        .priceStandard(book.getPriceStandard())
        .cover(book.getCover())
        .pubDate(book.getPubDate())
        .build();
  }
}
