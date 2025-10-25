package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.entity.MemberWish;
import java.util.List;

public class MemberWishFixture {

  public static MemberWish DEFAULT_MEMBER_WISH() {
    return MemberWish.builder()
        .id(1L)
        .memberId(MEMBER_ID)
        .bookId(1L)
        .build();
  }

  public static MemberWish SECOND_MEMBER_WISH() {
    return MemberWish.builder()
        .id(2L)
        .memberId(MEMBER_ID)
        .bookId(2L)
        .build();
  }

  public static final List<MemberWish> DEFAULT_MEMBER_WISHES = List.of(DEFAULT_MEMBER_WISH(), SECOND_MEMBER_WISH());
}
