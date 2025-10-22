package com.bob.domain.member.service;

import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.member.entity.MemberBook;
import com.bob.domain.member.repository.MemberBookRepository;
import com.bob.domain.member.service.dto.command.AllocateMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.ChangeMemberBookUsageCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByIdsCommand;
import com.bob.domain.member.service.dto.command.FreeMemberBookUsageByUsageIdCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBookCommand;
import com.bob.domain.member.service.dto.command.RemoveMemberBooksCommand;
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
import java.util.UUID;
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
    List<MemberBook> memberBooks = switch (query.key()) {
      case ALL -> memberBookReader.readMemberBooksByMemberId(query.memberId());
      case AVAILABLE -> memberBookReader.readAvailableMemberBooksByMemberId(query.memberId(), query.requires());
      case UNAVAILABLE -> memberBookReader.readUnavailableMemberBooksByMemberId(query.memberId(), query.requires());
    };
    return MemberBooksResponse.of(getMemberBookSummaries(memberBooks));
  }

  @Transactional(readOnly = true)
  public MemberBooksResponse readMemberBooksByIdsProcess(ReadMemberBooksByIdQuery query) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(query.ids());
    return MemberBooksResponse.of(getMemberBookSummaries(memberBooks));
  }

  private List<MemberBookSummary> getMemberBookSummaries(List<MemberBook> memberBooks) {
    List<Long> bookIds = memberBooks.stream().map(MemberBook::getBookId).toList();
    return MemberBookSummary.listFrom(memberBooks, bookPort.readBookSummaries(bookIds));
  }

  // TODO : 할당, 해제 분리
  @Transactional
  public void changeMemberBookUsageProcess(ChangeMemberBookUsageCommand command) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(command.memberBookIds());
    verifyMemberBookOwner(command.memberId(), command.memberBookIds(), memberBooks);
    if (!command.release())
      allocate(memberBooks, command.usageId());
    else
      memberBooks.forEach(mb -> mb.updateUsageId(null));
  }

  @Transactional
  public void allocateMemberBookUsageProcess(AllocateMemberBookUsageCommand command) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(command.ids());
    allocate(memberBooks, command.usageId());
  }

  private void allocate(List<MemberBook> memberBooks, Long usageId) {
    verifyMemberBookAvailable(memberBooks);
    verifyMemberBookIsFree(memberBooks, usageId);
    memberBooks.stream()
        .filter(mb -> !Objects.equals(mb.getUsageId(), usageId))
        .forEach(mb -> mb.updateUsageId(usageId));
  }

  private static void verifyMemberBookOwner(UUID memberId, List<Long> requestIds, List<MemberBook> memberBooks) {
    boolean isOwner = memberBooks.stream().allMatch(mb -> Objects.equals(mb.getMemberId(), memberId));
    if (!isOwner || requestIds.size() != memberBooks.size())
      throw new ApplicationException(ApplicationError.MEMBER_BOOK_ACCESS_DENIED);
  }

  private void verifyMemberBookAvailable(List<MemberBook> memberBooks) {
    memberBooks.stream()
        .filter(MemberBook::isRemove)
        .findFirst().ifPresent(mb -> {
          BookResponse book = bookPort.readBookSummary(mb.getBookId());
          throw new ApplicationException(ApplicationError.MEMBER_BOOK_UNAVAILABLE, book.title());
        });
  }

  private void verifyMemberBookIsFree(List<MemberBook> memberBooks, Long usageId) {
    memberBooks.stream()
        .filter(mb -> mb.getUsageId() != null && !Objects.equals(mb.getUsageId(), usageId))
        .findFirst().ifPresent(mb -> {
          BookResponse book = bookPort.readBookSummary(mb.getBookId());
          throw new ApplicationException(ApplicationError.MEMBER_BOOK_ALREADY_USE, mb.getUsageId(), book.title());
        });
  }

  @Transactional
  public void freeMemberBookUsageByIdsProcess(FreeMemberBookUsageByIdsCommand command) {
    memberBookRepository.freeUsageByIdIn(command.ids());
  }

  @Transactional
  public void freeMemberBookUsageByUsageIdProcess(FreeMemberBookUsageByUsageIdCommand command) {
    memberBookRepository.freeUsageByUsageId(command.usageId());
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

  @Transactional
  public void removeMemberBooksProcess(RemoveMemberBooksCommand command) {
    List<MemberBook> memberBooks = memberBookReader.readMemberBooksByBookIds(command.ids());
    memberBooks.forEach(MemberBook::remove);
  }
}
