package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import java.time.LocalDate;

public class BookCreateCommandFixture {

  public static CreateBookCommand defaultBookCreateCommand() {
    return new CreateBookCommand(
        DEFAULT_ISBN,
        "객체지향의 사실과 오해",
        "저우즈밍",
        "설명",
        10000,
        "https://image.url",
        LocalDate.now()
    );
  }
}
