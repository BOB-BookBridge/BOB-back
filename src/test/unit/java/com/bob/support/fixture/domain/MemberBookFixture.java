package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.entity.UsageStatus;

public class MemberBookFixture {

  public static MemberBook DEFAULT_MEMBER_BOOK = MemberBook.builder()
      .id(1L)
      .memberId(MEMBER_ID)
      .bookId(1L)
      .status(UsageStatus.FREE)
      .build();

  public static MemberBook NEW_MEMBER_BOOK = MemberBook.builder()
      .id(2L)
      .memberId(MEMBER_ID)
      .bookId(2L)
      .status(UsageStatus.FREE)
      .build();

  public static MemberBook IN_TRADE_BOOK = MemberBook.builder()
      .id(3L)
      .memberId(MEMBER_ID)
      .bookId(3L)
      .status(UsageStatus.IN_TRADE)
      .build();
}
