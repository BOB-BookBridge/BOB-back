package com.bob.domain.book.service.dto.command;

import com.bob.domain.book.entity.Book;
import java.time.LocalDate;

public record CreateBookCommand(
    String isbn13,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public Book toBook() {
    return Book.builder()
        .isbn13(isbn13)
        .title(title)
        .author(author)
        .description(description)
        .priceStandard(priceStandard)
        .cover(cover)
        .pubDate(pubDate)
        .build();
  }
}
