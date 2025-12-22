package com.bob.core.bookcase.application.port.out;

import java.time.LocalDate;
import java.util.List;

import com.bob.core.bookcase.application.port.result.BookcaseItemResult;

public interface BookcaseItemBookPort {

    Long register(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    );

    List<BookcaseItemResult> readBooks(List<Long> bookIds);

    BookcaseItemResult readBook(Long bookId);
}
