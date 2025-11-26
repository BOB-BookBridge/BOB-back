package com.bob.core.domain.bookcase;

import static com.bob.core.domain.bookcase.BookcaseItem.createBookcaseItem;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.support.fixture.bookcase.domain.BookcaseItemFixture;

@DisplayName("책장 물품 도메인 테스트")
class BookcaseItemTest {

    @Test
    void 책장_물품_생성() {
        UUID memberId = MEMBER_ID;
        Long bookId = 1L;

        BookcaseItem bookcaseItem = createBookcaseItem(memberId, bookId, "BEST");

        assertThat(bookcaseItem.getMemberId()).isEqualTo(memberId);
        assertThat(bookcaseItem.getBookId()).isEqualTo(bookId);
        assertThat(bookcaseItem.getStatus()).isEqualTo(BookStatus.BEST);
    }

    @Test
    void 책장_물품_소유_확인() {
        UUID memberId = MEMBER_ID;

        BookcaseItem bookcaseItem = createBookcaseItem(MEMBER_ID, 1L, "BEST");

        assertThat(bookcaseItem.isOwner(memberId)).isTrue();
        assertThat(bookcaseItem.isOwner(OTHER_MEMBER_ID)).isFalse();
    }

    @Test
    void 책장_물품_사용처_업데이트() {
        BookcaseItem bookcaseItem = BookcaseItemFixture.createBookcaseItem();

        assertThat(bookcaseItem.getUsageId()).isNull();

        bookcaseItem.updateUsageId(1L);

        assertThat(bookcaseItem.getUsageId()).isEqualTo(1L);
    }

    @Test
    void 책장_물품_삭제() {
        BookcaseItem bookcaseItem = BookcaseItemFixture.createBookcaseItem();
        assertThat(bookcaseItem.isDeleted()).isFalse();

        bookcaseItem.delete();

        assertThat(bookcaseItem.isDeleted()).isTrue();
    }

    @Test
    void 책장_물품_삭제_가능_여부_확인() {
        BookcaseItem bookcaseItem = createBookcaseItem(MEMBER_ID, 1L, "BEST");
        assertThat(bookcaseItem.isDeletable()).isTrue();

        bookcaseItem.updateUsageId(1L);

        assertThat(bookcaseItem.isDeletable()).isFalse();
    }
}
