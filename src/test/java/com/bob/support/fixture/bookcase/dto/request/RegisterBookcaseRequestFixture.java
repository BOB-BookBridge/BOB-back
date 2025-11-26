package com.bob.support.fixture.bookcase.dto.request;

import static com.bob.support.fixture.book.domain.BookFixture.DEFAULT_ISBN;

import java.time.LocalDate;

import com.bob.core.adapter.bookcase.api.request.RegisterBookcaseRequest;

public class RegisterBookcaseRequestFixture {

    public static RegisterBookcaseRequest createRegisterBookcaseRequest(String status) {
        return new RegisterBookcaseRequest(status, DEFAULT_ISBN, "제목", "저자", "설명",
            10000, "http://cover.jpg", LocalDate.of(2025, 1, 1));
    }
}
