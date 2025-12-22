package com.bob.support.fixture.book.domain;

import java.time.LocalDate;

import com.bob.core.book.domain.Book;

public class BookFixture {

    public static final String DEFAULT_ISBN = "1234567890123";

    public static Book createBook(String isbn, String title) {
        return Book.createBook(isbn, title, "저자", "설명", 10000, "https://cover.url", LocalDate.now());
    }

    public static Book createBook(String isbn) {
        return createBook(isbn, "제목");
    }

    public static Book createBook() {
        return createBook(DEFAULT_ISBN, "제목");
    }
}
