package com.bob.core.book.application.port.in;

import java.util.List;
import java.util.Optional;

import com.bob.core.book.application.dto.query.ReadBooksQuery;
import com.bob.core.book.domain.Book;

public interface BookReader {

    List<Book> readAll(List<Long> ids);

    Book read(Long id);

    Optional<Book> read(String isbn);

    List<Long> readAllIdsByQuery(ReadBooksQuery query);
}
