package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.BookFixture.DEFAULT_ISBN;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.post.service.dto.command.CreatePostCommand;
import java.time.LocalDate;
import java.util.UUID;

public class CreatePostCommandFixture {

  public static CreatePostCommand defaultCreatePostCommand() {
    return CreatePostCommand.builder()
        .memberId(MEMBER_ID)
        .categoryId(1)
        .sellPrice(38000)
        .bookStatus("최상")
        .postDescription("거의 새 책입니다.")
        .bookIsbn("1020366172839")
        .bookTitle("JVM 밑바닥까지 파헤치기")
        .bookAuthor("저우즈밍")
        .bookDescription("JVM 핵심 원리 소개")
        .bookPriceStandard(43000)
        .bookCover("https://image.jpg")
        .bookPubDate(LocalDate.of(2024, 4, 29))
        .build();
  }

  public static CreatePostCommand defaultCreatePostCommand(UUID memberId, Integer categoryId) {
    return CreatePostCommand.builder()
        .memberId(memberId)
        .categoryId(categoryId)
        .sellPrice(39000)
        .bookStatus("최상")
        .postDescription("신품급 도서 팝니다.")
        .bookIsbn("1020366172839")
        .bookTitle("JVM 밑바닥까지 파헤치기")
        .bookAuthor("저우즈밍")
        .bookDescription("핵심 JVM 원리 설명")
        .bookPriceStandard(43000)
        .bookCover("https://cover.url")
        .bookPubDate(LocalDate.of(2024, 4, 29))
        .build();
  }
}
