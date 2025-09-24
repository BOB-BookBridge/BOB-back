package com.bob.domain.member.entity;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MemberBook 도메인 테스트")
class MemberBookTest {

  @Test
  void 회원_도서_생성() {
    // given
    UUID memberId = MEMBER_ID;
    Long bookId = 1L;

    // when
    MemberBook result = MemberBook.of(memberId, bookId, "BEST");

    // then
    assertThat(result.getMemberId()).isEqualTo(memberId);
    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getStatus()).isEqualTo(BookStatus.BEST);
  }
}