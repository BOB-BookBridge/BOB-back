package com.bob.support.fixture.domain;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.service.dto.response.BookResponse;
import java.time.LocalDate;

public class BookFixture {
  public static final String DEFAULT_ISBN = "9788998139766";

  public static Book DEFAULT_BOOK = Book.builder()
      .id(1L)
      .isbn13(DEFAULT_ISBN)
      .title("객체지향의 사실과 오해")
      .author("조영호")
      .description("설명")
      .priceStandard(10000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static Book SECOND_BOOK = Book.builder()
      .id(2L)
      .isbn13("9788966261208")
      .title("오브젝트")
      .author("조영호")
      .description("설명")
      .priceStandard(12000)
      .cover("https://object.url")
      .pubDate(LocalDate.now())
      .build();
}
