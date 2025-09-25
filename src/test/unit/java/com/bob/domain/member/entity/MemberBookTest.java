package com.bob.domain.member.entity;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
    assertThat(result.getBookStatus()).isEqualTo(BookStatus.BEST);
  }

  @Test
  void 회원_도서_본인_확인() {
    // given
    UUID owner = MEMBER_ID;
    MemberBook mb = MemberBook.of(owner, 1L, "BEST");

    // then
    assertThat(mb.isOwner(owner)).isTrue();
  }

  @Test
  void 회원_도서_본인_확인_시_타인의_경우_false를_반환한다() {
    // given
    MemberBook mb = MemberBook.of(MEMBER_ID, 1L, "BEST");

    // when
    UUID other = UUID.randomUUID();

    // then
    assertThat(mb.isOwner(other)).isFalse();
  }

  @Test
  void 회원_도서_삭제_가능() {
    // given
    MemberBook mb = MemberBook.of(MEMBER_ID, 1L, "BEST");

    // then
    assertThat(mb.isRemovable()).isTrue();
  }

  @Test
  void 회원_도서_삭제_불가() {
    // given
    MemberBook mb = MemberBook.of(MEMBER_ID, 1L, "BEST");
    ReflectionTestUtils.setField(mb, "usageId", 777L);

    // then
    assertThat(mb.isRemovable()).isFalse();
  }
}