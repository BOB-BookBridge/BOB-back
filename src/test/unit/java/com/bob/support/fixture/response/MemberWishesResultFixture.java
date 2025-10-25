package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberWishFixture.DEFAULT_MEMBER_WISHES;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;

import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;

public class MemberWishesResultFixture {

  public static MemberWishesResult DEFAULT_MEMBER_WISHES_RESULT =
      MemberWishesResult.from(MemberWishSummary.listFrom(DEFAULT_MEMBER_WISHES, DEFAULT_BOOK_RESPONSES));
}
