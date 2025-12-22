package com.bob.core.book.application;

import static com.bob.core.book.domain.Book.createBook;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.book.application.dto.command.RegisterBookCommand;
import com.bob.core.book.application.port.in.BookReader;
import com.bob.core.book.application.port.in.BookRegister;
import com.bob.core.book.domain.Book;
import com.bob.core.book.domain.repository.BookRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCommandService implements BookRegister {

    private final BookRepository bookRepository;
    private final BookReader bookReader;

    public Book register(RegisterBookCommand command) {
        return bookReader.read(command.isbn())
            .orElseGet(() -> bookRepository.save(createBook(
                command.isbn(), command.title(), command.author(),
                command.description(), command.priceStandard(), command.cover(), command.pubDate()))
            );
    }
}
