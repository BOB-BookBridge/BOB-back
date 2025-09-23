package com.bob.domain.book.service;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.SearchBookQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.service.reader.BookReader;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookService implements BookWriteUseCase, BookReadUseCase {

  private final BookRepository bookRepository;
  private final BookReader bookReader;

  @Transactional
  public Long createBookProcess(CreateBookCommand command) {
    Book book = bookReader.readOptionalBookByIsbn(command.isbn13())
        .orElseGet(() -> bookRepository.save(command.toBook()));
    return book.getId();
  }

  @Transactional(readOnly = true)
  public BookResponse readBookProcess(ReadBookDetailQuery query) {
    Book book = bookReader.readBookById(query.id());
    return BookResponse.from(book);
  }

  @Transactional(readOnly = true)
  public List<Long> searchBookIdsProcess(SearchBookQuery query) {
    return bookReader.searchBookIdsByKeyword(query.key(), query.keyword());
  }
}
