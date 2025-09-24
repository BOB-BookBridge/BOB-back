package com.bob.domain.member.service;

import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberBookService implements MemberBookWriteUseCase {

  private final MemberBookRepository memberBookRepository;

  @Transactional
  public void registerMemberBookProcess(RegisterMemberBookCommand command) {
    memberBookRepository.save(command.toMemberBook());
  }
}
