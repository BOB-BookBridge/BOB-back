package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import java.time.LocalDate;

public class CreateBookCommandFixture {

  public static CreateBookCommand DEFAULT_CREATE_BOOK_COMMAND = CreateBookCommand.builder()
      .isbn13(DEFAULT_ISBN)
      .title("객체지향의 사실과 오해")
      .author("조영호")
      .description("설명")
      .priceStandard(10000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();

  public static CreateBookCommand NEW_CREATE_BOOK_COMMAND = CreateBookCommand.builder()
      .isbn13("9781998039656")
      .title("신규책")
      .author("BOB")
      .description("새로 등록되는 책")
      .priceStandard(10000)
      .cover("https://cover.png")
      .pubDate(LocalDate.now())
      .build();
}
