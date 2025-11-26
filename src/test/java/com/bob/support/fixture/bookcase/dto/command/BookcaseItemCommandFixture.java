package com.bob.support.fixture.bookcase.dto.command;

import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import java.time.LocalDate;
import java.util.UUID;

import com.bob.core.application.bookcase.dto.command.RegisterBookcaseItemCommand;

public class BookcaseItemCommandFixture {

    public static RegisterBookcaseItemCommand createRegisterItemCommand(UUID memberId, String isbn, String title) {
        return RegisterBookcaseItemCommand.builder()
            .memberId(memberId)
            .status("BEST")
            .isbn(isbn)
            .title(title)
            .author("저자")
            .description("설명")
            .priceStandard(10000)
            .cover("https://cover.url")
            .pubDate(LocalDate.now())
            .build();
    }

    public static RegisterBookcaseItemCommand createRegisterItemCommand(String isbn, String title) {
        return createRegisterItemCommand(MEMBER_ID, isbn, title);
    }

    public static RegisterBookcaseItemCommand createRegisterItemCommand(String isbn) {
        return createRegisterItemCommand(isbn, "제목");
    }

    public static RegisterBookcaseItemCommand createRegisterItemCommand() {
        return createRegisterItemCommand(DEFAULT_ISBN);
    }

    public static RegisterBookcaseItemCommand createRegisterItemCommand(UUID memberId) {
        return createRegisterItemCommand(memberId, DEFAULT_ISBN, "제목");
    }
}
