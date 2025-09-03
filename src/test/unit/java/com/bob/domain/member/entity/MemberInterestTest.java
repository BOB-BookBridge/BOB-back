package com.bob.domain.member.entity;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberInterestTest {

  @Test
  void 회원_관심사_생성() {
    // given
    Long id = 123L;
    String displayName = "Java";

    // when
    MemberInterest memberInterest = MemberInterest.of(MEMBER_ID, id, displayName);

    // then
    assertThat(memberInterest.getMemberId()).isEqualTo(MEMBER_ID);
    assertThat(memberInterest.getInterestId()).isEqualTo(id);
    assertThat(memberInterest.getDisplayName()).isEqualTo("Java");
  }

  @Test
  void 회원_관심사_이름_표현() {
    // given
    Long interestId = 99L;
    String rawDisplay = "  Java  ";

    // when
    MemberInterest mi = MemberInterest.of(MEMBER_ID, interestId, rawDisplay);

    // then
    assertThat(mi.getDisplayName()).isEqualTo("  Java  ");
  }
}
