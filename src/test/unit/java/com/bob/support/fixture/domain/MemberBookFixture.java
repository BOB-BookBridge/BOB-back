package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.entity.MemberBook;

public class MemberBookFixture {

  public static MemberBook DEFAULT_MEMBER_BOOK = MemberBook.builder()
      .id(1L)
      .memberId(MEMBER_ID)
      .bookId(1L)
      .build();

  public static MemberBook NEW_MEMBER_BOOK = MemberBook.builder()
      .id(2L)
      .memberId(MEMBER_ID)
      .bookId(2L)
      .build();
}
