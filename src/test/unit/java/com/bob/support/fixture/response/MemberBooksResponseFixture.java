package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberBookFixture.CUSTOM_MEMBER_BOOK;
import static com.bob.support.fixture.domain.MemberBookFixture.DEFAULT_MEMBER_BOOK_RESPONSES;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.response.BookResponseFixture.CUSTOM_BOOK_RESPONSES;
import static com.bob.support.fixture.response.BookResponseFixture.DEFAULT_BOOK_RESPONSES;

import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import java.util.List;

public class MemberBooksResponseFixture {

  public static MemberBooksResponse DEFAULT_MEMBER_BOOKS_RESPONSE =
      MemberBooksResponse.of(MemberBookSummary.listFrom(DEFAULT_MEMBER_BOOK_RESPONSES, DEFAULT_BOOK_RESPONSES));

  public static MemberBooksResponse SINGLE_MEMBER_BOOKS_RESPONSE =
      MemberBooksResponse.of(MemberBookSummary.listFrom(List.of(DEFAULT_MEMBER_BOOK_RESPONSES.get(0)),
          List.of(DEFAULT_BOOK_RESPONSES.get(0))));

  public static MemberBooksResponse CUSTOM_MEMBER_BOOKS_RESPONSE(Long id, Long bookId) {
    return MemberBooksResponse.of(MemberBookSummary.listFrom(List.of(CUSTOM_MEMBER_BOOK(OTHER_MEMBER_ID, id, bookId)), CUSTOM_BOOK_RESPONSES(bookId)));
  }
}
