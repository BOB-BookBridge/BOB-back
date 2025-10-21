package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.entity.BookStatus;
import java.util.List;

public class MemberBookFixture {

  public static MemberBook DEFAULT_MEMBER_BOOK() {
    return MemberBook.builder()
        .id(1L)
        .memberId(MEMBER_ID)
        .bookId(1L)
        .status(BookStatus.BEST)
        .isRemove(false)
        .build();
  }

  public static MemberBook NEW_MEMBER_BOOK() {
    return MemberBook.builder()
        .id(2L)
        .memberId(MEMBER_ID)
        .bookId(2L)
        .status(BookStatus.HIGH)
        .isRemove(false)
        .build();
  }

  public static MemberBook SAME_IN_TRADE_BOOK() {
    return MemberBook.builder()
        .id(3L)
        .memberId(MEMBER_ID)
        .bookId(3L)
        .status(BookStatus.LOW)
        .usageId(1L)
        .isRemove(false)
        .build();
  }

  public static MemberBook DIFF_IN_TRADE_BOOK() {
    return MemberBook.builder()
        .id(4L)
        .memberId(MEMBER_ID)
        .bookId(4L)
        .status(BookStatus.BEST)
        .usageId(3L)
        .isRemove(false)
        .build();
  }

  public static MemberBook REMOVED_BOOK() {
    return MemberBook.builder()
        .id(5L)
        .memberId(MEMBER_ID)
        .bookId(5L)
        .status(BookStatus.BEST)
        .usageId(null)
        .isRemove(true)
        .build();
  }

  public static final List<MemberBook> DEFAULT_MEMBER_BOOK_RESPONSES = List.of(DEFAULT_MEMBER_BOOK(), NEW_MEMBER_BOOK());
  public static final List<MemberBook> IN_TRADE_MEMBER_BOOK_RESPONSES = List.of(SAME_IN_TRADE_BOOK(), DIFF_IN_TRADE_BOOK());
}
