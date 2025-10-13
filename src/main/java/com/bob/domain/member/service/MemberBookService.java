package com.bob.domain.member.service;

import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookUsageCommand;
import com.bob.domain.member.service.dto.query.ReadMemberBooksByIdQuery;
import com.bob.domain.member.service.dto.query.ReadMemberBooksQuery;
import com.bob.domain.member.service.dto.response.MemberBooksResponse;
import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.service.reader.MemberBookReader;
import com.bob.domain.member.usecase.MemberBookModifyUseCase;
import com.bob.domain.member.usecase.MemberBookReadUseCase;
import com.bob.domain.member.usecase.MemberBookRemoveUseCase;
import com.bob.domain.member.usecase.MemberBookWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberBookService implements MemberBookWriteUseCase, MemberBookReadUseCase, MemberBookModifyUseCase, MemberBookRemoveUseCase {

  private final MemberBookRepository memberBookRepository;
  private final MemberBookReader memberBookReader;

  private final MemberBookPort bookPort;

  @Transactional
  public Long registerMemberBookProcess(RegisterMemberBookCommand command) {
    Long bookId = command.bookId();
    if (bookId == null) {
      bookId = createBook(command);
    }
    MemberBook memberBook = memberBookRepository.save(MemberBook.of(command.memberId(), bookId, command.status()));
    return memberBook.getId();
  }

  private Long createBook(RegisterMemberBookCommand command) {
    return bookPort.createBook(command.toCreateBookCommand());
  }

  @Transactional(readOnly = true)
  public MemberBooksResponse readMemberBooksProcess(ReadMemberBooksQuery query) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByMemberId(query.memberId());
    List<MemberBookSummary> summaries = getMemberBookSummaries(memberBooks);
    return MemberBooksResponse.of(summaries);
  }

  @Transactional(readOnly = true)
  public MemberBooksResponse readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery query) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(query.ids());
    List<MemberBookSummary> summaries = getMemberBookSummaries(memberBooks);
    return MemberBooksResponse.of(summaries);
  }

  private List<MemberBookSummary> getMemberBookSummaries(List<MemberBook> memberBooks) {
    List<Long> bookIds = memberBooks.stream().map(MemberBook::getBookId).toList();
    List<MemberBookSummary> summaries = MemberBookSummary.listFrom(memberBooks, bookPort.readBookSummaries(bookIds));
    return summaries;
  }

  @Transactional
  public void changeMemberBookUsageProcess(ChangeMemberBookUsageCommand command) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(command.memberBookIds());
    memberBooks.stream()
        .filter(mb -> mb.getUsageId() != null && !Objects.equals(mb.getUsageId(), command.usageId()))
        .findFirst().ifPresent(mb -> {
          BookResponse book = bookPort.readBookSummary(mb.getBookId());
          throw new ApplicationException(ApplicationError.MEMBER_BOOK_ALREADY_USE, mb.getUsageId(), book.title());
        });
    memberBooks.stream()
        .filter(mb -> !Objects.equals(mb.getUsageId(), command.usageId()))
        .forEach(mb -> mb.updateUsageId(command.usageId()));
  }

  @Transactional
  public void removeMemberBookUsageProcess(RemoveMemberBookUsageCommand command) {
    memberBookRepository.clearUsageId(command.usageId());
  }

  @Transactional
  public void removeMemberBookProcess(RemoveMemberBookCommand command) {
    MemberBook memberBook = memberBookReader.readMemberBookById(command.id());

    if (!memberBook.isOwner(command.memberId()))
      throw new ApplicationException(ApplicationError.OBJECT_ACCESS_DENIED);
    if (!memberBook.isRemovable())
      throw new ApplicationException(ApplicationError.UNREMOVABLE_MEMBER_BOOK, memberBook.getUsageId());
    memberBook.remove();
  }
}
