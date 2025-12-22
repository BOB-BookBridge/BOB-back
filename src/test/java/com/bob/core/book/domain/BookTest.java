package com.bob.core.book.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("책 도메인 테스트")
class BookTest {

    @Test
    void 책_등록() {
        Book book = Book.createBook("0000000000000", "제목", "저자", "설명", 10000, "http://image.url", LocalDate.now());

        assertAll(
            () -> assertEquals("0000000000000", book.getIsbn()),
            () -> assertEquals("제목", book.getTitle()),
            () -> assertEquals("저자", book.getAuthor()),
            () -> assertEquals("설명", book.getDescription()),
            () -> assertEquals(10000, book.getPriceStandard()),
            () -> assertEquals("http://image.url", book.getCover()),
            () -> assertNotNull(book.getPubDate())
        );
    }
}
