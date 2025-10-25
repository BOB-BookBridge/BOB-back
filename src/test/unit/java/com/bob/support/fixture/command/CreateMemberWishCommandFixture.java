package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import java.time.LocalDate;

public class CreateMemberWishCommandFixture {


  public static final CreateMemberWishCommand DEFAULT_CREATE_WISH_COMMAND = CreateMemberWishCommand.builder()
      .memberId(MEMBER_ID)
      .isbn(DEFAULT_ISBN)
      .title("제목")
      .author("작가")
      .description("설명")
      .priceStandard(10000)
      .cover("https://image.url")
      .pubDate(LocalDate.now())
      .build();
}
