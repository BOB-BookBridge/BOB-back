package com.bob.integration.adapter.bookcase;

import static com.bob.core.book.application.dto.command.RegisterBookCommand.of;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.book.application.dto.command.RegisterBookCommand;
import com.bob.core.book.application.port.in.BookReader;
import com.bob.core.book.application.port.in.BookRegister;
import com.bob.core.book.domain.Book;
import com.bob.core.bookcase.application.port.out.BookcaseItemBookPort;
import com.bob.core.bookcase.application.port.result.BookcaseItemResult;

@Component
@RequiredArgsConstructor
public class BookcaseItemBookAdapter implements BookcaseItemBookPort {

    private final BookRegister bookRegister;

    private final BookReader bookReader;

    @Override
    public Long register(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    ) {
        RegisterBookCommand command = of(isbn, title, author, description, priceStandard, cover, pubDate);

        Book book = bookRegister.register(command);

        return book.getId();
    }

    @Override
    public List<BookcaseItemResult> readBooks(List<Long> bookIds) {
        List<Book> books = bookReader.readAll(bookIds);

        return books.stream().map(this::convert).toList();
    }

    @Override
    public BookcaseItemResult readBook(Long bookId) {
        Book book = bookReader.read(bookId);
        return convert(book);
    }

    private BookcaseItemResult convert(Book book) {
        return new BookcaseItemResult(
            book.getId(), book.getIsbn(), book.getTitle(), book.getAuthor(),
            book.getDescription(), book.getPriceStandard(), book.getCover(), book.getPubDate()
        );
    }
}
