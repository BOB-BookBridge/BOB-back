package com.bob.support.fixture.bookcase.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import com.bob.core.domain.bookcase.BookcaseItem;

public class BookcaseItemFixture {

    public static BookcaseItem createBookcaseItem(Long bookId) {
        return BookcaseItem.createBookcaseItem(MEMBER_ID, bookId, "BEST");
    }

    public static BookcaseItem createBookcaseItem() {
        return createBookcaseItem(1L);
    }
}
