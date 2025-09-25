package com.bob.domain.member.service;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberBookService implements MemberBookWriteUseCase {

  private final MemberBookRepository memberBookRepository;

  private final MemberBookPort bookPort;

  @Transactional
  public void registerMemberBookProcess(RegisterMemberBookCommand command) {
    Long bookId = command.bookId();
    if (bookId == null) {
      bookId = createBook(command);
    }
    memberBookRepository.save(MemberBook.of(command.memberId(), bookId));
  }

  private Long createBook(RegisterMemberBookCommand command) {
    return bookPort.createBook(command.toCreateBookCommand());
  }
}
