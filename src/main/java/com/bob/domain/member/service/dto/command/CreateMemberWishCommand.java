package com.bob.domain.member.service.dto.command;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateMemberWishCommand(
    UUID memberId,
    String isbn,
    String title,
    String author,
    String description,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static CreateMemberWishCommand of(
      UUID memberId,
      String isbn,
      String title,
      String author,
      String description,
      int priceStandard,
      String cover,
      LocalDate pubDate
  ) {
    return CreateMemberWishCommand.builder()
        .memberId(memberId)
        .isbn(isbn)
        .title(title)
        .author(author)
        .description(description)
        .priceStandard(priceStandard)
        .cover(cover)
        .pubDate(pubDate)
        .build();
  }
}
