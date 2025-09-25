package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK_RESPONSES;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;

import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;

public class MemberBooksResponseFixture {

  public static MemberBooksResponse DEFAULT_MEMBER_BOOKS_RESPONSE =
      MemberBooksResponse.of(MemberBookSummary.listFrom(DEFAULT_MEMBER_BOOK_RESPONSES, DEFAULT_BOOK_RESPONSES));
}
