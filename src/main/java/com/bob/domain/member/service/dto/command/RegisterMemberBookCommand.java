package com.bob.domain.member.service.dto.command;

import com.bob.domain.member.entity.BookStatus;
import com.bob.domain.member.entity.MemberBook;
import java.util.UUID;

public record RegisterMemberBookCommand(
    UUID memberId,
    Long bookId,
    BookStatus status
) {

  public MemberBook toMemberBook() {
    return MemberBook.builder()
        .memberId(memberId)
        .bookId(bookId)
        .bookStatus(status)
        .build();
  }
  public static RegisterMemberBookCommand of(UUID memberId, Long id, String status) {
    return new RegisterMemberBookCommand(memberId, id, BookStatus.valueOf(status));
  }
}
