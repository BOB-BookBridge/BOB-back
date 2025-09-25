package com.bob.domain.member.service.reader;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberBookReader {

  private final MemberBookRepository repository;

  public List<MemberBook> readMemberBooksByMemberId(UUID memberId) {
    return repository.findByMemberId(memberId);
  }

  public MemberBook readMemberBookById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_OBJECT));
  }
}
