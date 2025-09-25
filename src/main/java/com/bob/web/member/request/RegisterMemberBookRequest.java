package com.bob.web.member.request;

import com.bob.domain.member.service.dto.command.RegisterMemberBookCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterMemberBookRequest(
    @NotBlank(message = "책 상태는 필수입니다.")
    String status,

    @NotBlank(message = "ISBN은 필수입니다.")
    String isbn,

    @NotBlank(message = "책 제목은 필수입니다.")
    String title,

    @NotBlank(message = "저자는 필수입니다.")
    String author,

    String description,

    @NotNull(message = "정가 정보는 필수입니다.")
    @PositiveOrZero(message = "정가는 0 이상이어야 합니다.")
    Integer priceStandard,

    @NotBlank(message = "표지 URL은 필수입니다.")
    String cover,

    @NotNull(message = "출판일은 필수입니다.")
    LocalDate pubDate
) {

  public RegisterMemberBookCommand toCommand(UUID memberId) {
    return RegisterMemberBookCommand.builder()
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
