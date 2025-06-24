package com.bob.web.post.request;

import com.bob.domain.post.service.dto.command.CreatePostCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePostRequest(
    @NotNull(message = "카테고리는 필수입니다.")
    Integer categoryId,

    @NotNull(message = "판매 가격은 필수입니다.")
    @PositiveOrZero(message = "판매 가격은 0 이상이어야 합니다.")
    Integer sellPrice,

    @NotBlank(message = "책 상태는 필수입니다.")
    String bookStatus,

    @Size(max = 1000, message = "게시글 설명은 1000자 이하로 입력해 주세요.")
    String postDescription,

    @Valid
    @NotNull(message = "책 정보는 필수입니다.")
    BookInfo book,

    String imageReferenceId
) {

  public CreatePostCommand toCommand(UUID memberId) {
    return CreatePostCommand.builder()
        .memberId(memberId)
        .categoryId(categoryId)
        .sellPrice(sellPrice)
        .postDescription(postDescription)
        .bookStatus(bookStatus)
        .bookIsbn(book.isbn())
        .bookTitle(book.title())
        .bookAuthor(book.author())
        .bookDescription(book.description())
        .bookPriceStandard(book.priceStandard())
        .bookCover(book.cover())
        .bookPubDate(book.pubDate())
        .imageReferenceId(imageReferenceId)
        .build();
  }
}

record BookInfo(
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

}


