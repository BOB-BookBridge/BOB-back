package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;

import com.bob.domain.book.service.dto.response.BookResponse;
import java.time.LocalDate;
import java.util.List;

public class BookResponseFixture {

  public static final BookResponse DEFAULT_BOOK_RESPONSE = BookResponse.builder()
      .id(1L)
      .isbn13(DEFAULT_ISBN)
      .title("객체지향의 사실과 오해")
      .author("조영호")
      .description("설명")
      .priceStandard(10000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static final BookResponse SECOND_BOOK_RESPONSE = BookResponse.builder()
      .id(2L)
      .isbn13("9788966261208")
      .title("오브젝트")
      .author("조영호")
      .description("설명")
      .priceStandard(12000)
      .cover("https://object.url")
      .pubDate(LocalDate.now())
      .build();

  public static final List<BookResponse> DEFAULT_BOOK_RESPONSES = List.of(DEFAULT_BOOK_RESPONSE, SECOND_BOOK_RESPONSE);
}
