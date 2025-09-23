package com.bob.domain.post.service.port.out;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.response.BookResponse;
import java.util.List;

public interface PostBookPort {

  Long createBook(CreateBookCommand command);

  BookResponse readBookSummary(Long bookId);

  List<Long> searchBookIds(String key, String keyword);
}
