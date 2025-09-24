package com.bob.web.book.adapter;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.book.usecase.BookWriteUseCase;
import com.bob.domain.member.service.port.out.MemberBookPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberBookAdapter implements MemberBookPort {

  private final BookWriteUseCase writeUseCase;

  @Override
  public Long createBook(CreateBookCommand command) {
    return writeUseCase.createBookProcess(command);
  }
}
