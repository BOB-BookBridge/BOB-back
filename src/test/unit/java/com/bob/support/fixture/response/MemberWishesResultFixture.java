package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberWishFixture.CUSTOM_MEMBER_WISH;
import static com.bob.support.fixture.domain.MemberWishFixture.DEFAULT_MEMBER_WISHES;
import static com.bob.support.fixture.response.BookResponseFixture.CUSTOM_BOOK_RESPONSE;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;

import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import java.util.List;

public class MemberWishesResultFixture {

  public static MemberWishesResult DEFAULT_MEMBER_WISHES_RESULT =
      MemberWishesResult.from(MemberWishSummary.listFrom(DEFAULT_MEMBER_WISHES, DEFAULT_BOOK_RESPONSES));

  public static MemberWishesResult CUSTOM_MEMBER_WISHES_RESULT(Long id, Long bookId) {
    return MemberWishesResult.from(
        MemberWishSummary.listFrom(List.of(CUSTOM_MEMBER_WISH(id, bookId)), List.of(CUSTOM_BOOK_RESPONSE(bookId))));
  }
}
