package com.bob.domain.book.service.reader;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.repository.BookRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookReader {

  private final BookRepository bookRepository;

  public Book readBookById(Long id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_OBJECT));
  }

  public Optional<Book> readOptionalBookByIsbn(String isbn) {
    return bookRepository.findByIsbn13(isbn);
  }

  public List<Long> searchBookIdsByKeyword(String key, String keyword) {
    return bookRepository.findIdsByKeyword(key, keyword);
  }
}
