package com.bob.core.domain.member;

import static com.bob.core.domain.member.MemberWish.createMemberWish;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 희망 도서 테스트")
class MemberWishTest {

    @Test
    void 회원_희망_도서_생성() {
        Long bookId = 1L;

        MemberWish wish = createMemberWish(bookId);

        assertThat(wish.getBookId()).isEqualTo(bookId);
    }
}
