package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;
import static com.bob.global.exception.response.ApplicationError.OBJECT_ACCESS_DENIED;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.dto.command.DeleteMemberWishCommand;
import com.bob.domain.member.service.dto.query.ReadMemberWishesQuery;
import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.usecase.MemberWishDeleteUseCase;
import com.bob.domain.member.usecase.MemberWishReadUseCase;
import com.bob.domain.member.usecase.MemberWishWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberWishService implements MemberWishWriteUseCase, MemberWishReadUseCase, MemberWishDeleteUseCase {

  private final MemberWishRepository repository;

  private final MemberBookPort bookPort;

  @Transactional
  public void createMemberWishProcess(CreateMemberWishCommand command) {
    Long bookId = createBook(command);
    verifyDuplicated(command.memberId(), bookId);
    MemberWish wish = MemberWish.create(command.memberId(), bookId);
    repository.save(wish);
  }

  private Long createBook(CreateMemberWishCommand command) {
    return bookPort.create(command.isbn(), command.title(), command.author(), command.description(),
        command.priceStandard(), command.cover(), command.pubDate());
  }

  private void verifyDuplicated(UUID memberId, Long itemId) {
    if (repository.existsByMemberIdAndBookId(memberId, itemId))
      throw new ApplicationException(MEMBER_WISH_DUPLICATED);
  }

  @Transactional(readOnly = true)
  public MemberWishesResult readWishesProcess(ReadMemberWishesQuery query) {
    List<MemberWish> wishes = repository.findAllByMemberId(query.memberId());
    List<MemberWishSummary> summaries = getMemberWishSummaries(wishes);
    return MemberWishesResult.from(summaries);
  }

  private List<MemberWishSummary> getMemberWishSummaries(List<MemberWish> wishes) {
    List<Long> bookIds = wishes.stream().map(MemberWish::getBookId).toList();
    return MemberWishSummary.listFrom(wishes, bookPort.readBookSummaries(bookIds));
  }

  @Transactional
  public void deleteWishProcess(DeleteMemberWishCommand command) {
    MemberWish wish = repository.findById(command.id())
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXIST_OBJECT));
    verifyOwner(wish.getMemberId(), command.memberId());
    repository.delete(wish);
  }

  private static void verifyOwner(UUID ownerId, UUID requesterId) {
    if (!ownerId.equals(requesterId))
      throw new ApplicationException(OBJECT_ACCESS_DENIED);
  }
}
