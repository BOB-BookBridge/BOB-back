package com.bob.domain.post.service.dto.command;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePostCommand(
    UUID memberId,
    Integer categoryId,
    Integer sellPrice,
    String postDescription,
    String bookStatus,
    String bookIsbn,
    String bookTitle,
    String bookAuthor,
    String bookDescription,
    Integer bookPriceStandard,
    String bookCover,
    LocalDate bookPubDate,
    List<String> fileNames
) {

  public CreateBookCommand toCreateBookCommand() {
    return CreateBookCommand.of(
        bookIsbn,
        bookTitle,
        bookAuthor,
        bookDescription,
        bookPriceStandard,
        bookCover,
        bookPubDate
    );
  }

  public RegisterMemberBookCommand toCreateMemberBookCommand(Long id) {
    return RegisterMemberBookCommand.of(memberId, id, bookStatus);
  }
}
