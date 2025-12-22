package com.bob.core.book.application;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.book.application.dto.query.ReadBooksQuery;
import com.bob.core.book.application.port.in.BookReader;
import com.bob.core.book.domain.Book;
import com.bob.core.book.domain.repository.BookRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService implements BookReader {

    private final BookRepository bookRepository;

    @Override
    public List<Book> readAll(List<Long> ids) {
        return bookRepository.findAllByIdIn(ids);
    }

    @Override
    public Book read(Long id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다. id : " + id));
    }

    @Override
    public Optional<Book> read(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Long> readAllIdsByQuery(ReadBooksQuery query) {
        return bookRepository.findIdsByKeyword(query.key(), query.keyword());
    }
}
