package com.bob.web.book.adapter;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.service.dto.query.ReadBooksQuery;
import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.book.usecase.BookReadUseCase;
import com.bob.domain.book.usecase.BookWriteUseCase;
import com.bob.domain.member.service.port.out.MemberBookPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberBookAdapter implements MemberBookPort {

  private final BookWriteUseCase writeUseCase;
  private final BookReadUseCase readUseCase;

  @Override
  public Long createBook(CreateBookCommand command) {
    return writeUseCase.createBookProcess(command);
  }

  @Override
  public List<BookResponse> readBookSummaries(List<Long> ids) {
    return readUseCase.readBooksProcess(ReadBooksQuery.of(ids));
  }
}
