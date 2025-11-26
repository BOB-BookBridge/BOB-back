package com.bob.core.application.book;

import static com.bob.core.domain.book.Book.createBook;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.book.dto.command.RegisterBookCommand;
import com.bob.core.application.book.port.in.BookReader;
import com.bob.core.application.book.port.in.BookRegister;
import com.bob.core.domain.book.Book;
import com.bob.core.domain.book.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookCommandService implements BookRegister {

    private final BookRepository bookRepository;
    private final BookReader bookReader;

    @Transactional
    public Book register(RegisterBookCommand command) {
        return bookReader.read(command.isbn())
            .orElseGet(() -> bookRepository.save(createBook(
                command.isbn(), command.title(), command.author(),
                command.description(), command.priceStandard(), command.cover(), command.pubDate()))
            );
    }
}
