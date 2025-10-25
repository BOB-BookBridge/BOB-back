package com.bob.web.book.adapter;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBookDetailQuery;
import com.bob.domain.book.service.dto.query.ReadBooksQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import com.bob.domain.member.service.port.out.MemberBookPort;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberBookAdapter implements MemberBookPort {

  private final BookWriteUseCase writeUseCase;
  private final BookReadUseCase readUseCase;

  @Override
  public Long create(String isbn13, String title, String author, String description, Integer priceStandard,
      String cover, LocalDate pubDate
  ) {
    CreateBookCommand command = CreateBookCommand.of(isbn13, title, author, description, priceStandard, cover, pubDate);
    return writeUseCase.createBookProcess(command);
  }

  @Override
  public List<BookResponse> readBookSummaries(List<Long> ids) {
    return readUseCase.readBooksProcess(ReadBooksQuery.of(ids));
  }

  @Override
  public BookResponse readBookSummary(Long id) {
    return readUseCase.readBookProcess(ReadBookDetailQuery.of(id));
  }
}
