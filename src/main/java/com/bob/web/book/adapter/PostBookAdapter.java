package com.bob.web.book.adapter;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.SearchBookQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import com.bob.domain.post.service.port.out.PostBookPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostBookAdapter implements PostBookPort {

  private final BookWriteUseCase writeUseCase;
  private final BookReadUseCase readUseCase;

  @Override
  public Long createBook(CreateBookCommand command) {
    return writeUseCase.createBookProcess(command);
  }

  @Override
  public BookResponse readBookSummary(Long id) {
    return readUseCase.readBookProcess(ReadBookDetailQuery.of(id));
  }

  @Override
  public List<Long> searchBookIds(String key, String keyword) {
    return readUseCase.searchBookIdsProcess(SearchBookQuery.of(key, keyword));
  }
}
