package com.bob.support.fixture.book.dto.command;

import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;

import java.time.LocalDate;

import com.bob.core.book.application.dto.command.RegisterBookCommand;

public class CreateBookCommandFixture {

    public static RegisterBookCommand createRegisterBookCommand() {
        return RegisterBookCommand.builder()
            .isbn(DEFAULT_ISBN)
            .title("제목")
            .author("저자")
            .description("설명")
            .priceStandard(10000)
            .cover("https://cover.url")
            .pubDate(LocalDate.now())
            .build();
    }
}
