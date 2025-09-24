package com.bob.domain.member.service.dto.command;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegisterMemberBookCommand(
    UUID memberId,
    Long bookId,
    String status,
    String isbn,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static RegisterMemberBookCommand of(UUID memberId, Long bookId, String status) {
    return RegisterMemberBookCommand.builder()
        .memberId(memberId)
        .bookId(bookId)
        .status(status)
        .build();
  }

  public CreateBookCommand toCreateBookCommand() {
    return CreateBookCommand.of(isbn, title, author, description, priceStandard, cover, pubDate);
  }
}
