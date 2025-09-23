package com.bob.domain.post.service.dto.command;

import static com.bob.domain.post.entity.status.Status.ACTIVE;
import static com.bob.domain.post.entity.status.TradeProgress.READY;

import com.bob.domain.book.service.dto.command.CreateBookCommand;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.BookStatus;
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

  public Post toPost(Category category, Long bookId, UUID sellerId, Integer emdId) {
    return Post.builder()
        .title(bookTitle)
        .status(ACTIVE)
        .category(category)
        .bookId(bookId)
        .bookStatus(BookStatus.from(bookStatus))
        .sellerId(sellerId)
        .tradeProgress(READY)
        .sellPrice(sellPrice)
        .description(postDescription)
        .registrationAreaId(emdId)
        .thumbnailUrl(bookCover)
        .build();
  }

  public CreateBookCommand toCreateBookCommand() {
    return new CreateBookCommand(
        bookIsbn,
        bookTitle,
        bookAuthor,
        bookDescription,
        bookPriceStandard,
        bookCover,
        bookPubDate
    );
  }
}
