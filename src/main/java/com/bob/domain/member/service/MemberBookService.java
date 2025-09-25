package com.bob.domain.member.service;

import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.service.reader.MemberBookReader;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberBookService implements MemberBookWriteUseCase, MemberBookReadUseCase {

  private final MemberBookRepository memberBookRepository;
  private final MemberBookReader memberBookReader;

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

  @Transactional(readOnly = true)
  public MemberBooksResponse readMemberBooksProcess(ReadMemberBooksQuery query) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByMemberId(query.memberId());
    List<Long> bookIds = memberBooks.stream().map(MemberBook::getBookId).toList();
    List<MemberBookSummary> summaries = MemberBookSummary.listFrom(memberBooks, bookPort.readBookSummaries(bookIds));
    return MemberBooksResponse.of(summaries);
  }
}
