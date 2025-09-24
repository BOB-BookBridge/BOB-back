package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_BOOK;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import java.time.LocalDate;

public class RegisterMemberBookCommandFixture {

  public static RegisterMemberBookCommand DEFAULT_REGISTER_MEMBER_BOOK_COMMAND = RegisterMemberBookCommand.builder()
      .memberId(MEMBER_ID)
      .bookId(4L)
      .status("BEST")
      .isbn(DEFAULT_BOOK.getIsbn13())
      .title(DEFAULT_BOOK.getTitle())
      .author(DEFAULT_BOOK.getAuthor())
      .description(DEFAULT_BOOK.getDescription())
      .priceStandard(DEFAULT_BOOK.getPriceStandard())
      .cover(DEFAULT_BOOK.getCover())
      .pubDate(DEFAULT_BOOK.getPubDate())
      .build();

  public static RegisterMemberBookCommand REGISTER_MEMBER_BOOK_COMMAND_WITH_NULL_BOOK_ID = RegisterMemberBookCommand.builder()
      .memberId(MEMBER_ID)
      .bookId(null)
      .status("BEST")
      .isbn("9781998039656")
      .title("신규책")
      .author("BOB")
      .description("새로 등록되는 책")
      .priceStandard(10000)
      .cover("https://cover.png")
      .pubDate(LocalDate.now())
      .build();
}
