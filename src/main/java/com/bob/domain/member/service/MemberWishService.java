package com.bob.domain.member.service;

import static com.bob.global.exception.response.ApplicationError.MEMBER_WISH_DUPLICATED;

import com.bob.domain.member.entity.MemberWish;
import com.bob.domain.member.repository.MemberWishRepository;
import com.bob.domain.member.service.dto.command.CreateMemberWishCommand;
import com.bob.domain.member.service.port.out.MemberBookPort;
import com.bob.domain.member.usecase.MemberWishWriteUseCase;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberWishService implements MemberWishWriteUseCase {

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
}
