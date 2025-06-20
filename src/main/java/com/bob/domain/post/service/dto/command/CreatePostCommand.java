package com.bob.domain.post.service.dto.command;

import static com.bob.domain.post.entity.status.PostStatus.READY;

import com.bob.domain.book.entity.Book;
import com.bob.domain.book.service.dto.BookCreateCommand;
import com.bob.domain.category.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.BookStatus;
import java.time.LocalDate;
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
    LocalDate bookPubDate
) {

  public Post toPost(Book book, Category category, UUID sellerId, Integer emdId) {
    return Post.builder()
        .book(book)
        .sellerId(sellerId)
        .category(category)
        .bookStatus(BookStatus.from(bookStatus))
        .postStatus(READY)
        .sellPrice(sellPrice)
        .description(postDescription)
        .registrationAreaId(emdId)
        .thumbnailUrl(bookCover)
        .build();
  }

  public BookCreateCommand toBookCreateCommand() {
    return new BookCreateCommand(
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
