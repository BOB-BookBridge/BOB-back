package com.bob.support.fixture.member.dto.command;

import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;

import java.time.LocalDate;

import com.bob.core.application.member.dto.command.RegisterMemberWishCommand;

public class RegisterMemberWishCommandFixture {

    public static RegisterMemberWishCommand createRegisterMemberWishCommand(String isbn) {
        return RegisterMemberWishCommand.builder()
            .isbn(isbn)
            .title("제목")
            .author("저자")
            .description("설명")
            .priceStandard(10000)
            .cover("https://cover.png")
            .pubDate(LocalDate.now())
            .build();
    }

    public static RegisterMemberWishCommand createRegisterMemberWishCommand() {
        return createRegisterMemberWishCommand(DEFAULT_ISBN);
    }
}
