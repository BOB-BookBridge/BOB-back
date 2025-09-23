package com.bob.support.fixture.domain;

import static com.bob.domain.post.entity.status.Status.ACTIVE;
import static com.bob.domain.post.entity.status.TradeProgress.READY;
import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;

import com.bob.domain.book.entity.Book;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.TradeProgress;
import java.util.List;
import java.util.UUID;

public class PostFixture {

  public static Post defaultPost(Category category, Book book, UUID sellerId, Integer emdId) {
    return Post.builder()
        .status(ACTIVE)
        .title(book.getTitle())
        .category(category)
        .sellerId(sellerId)
        .bookId(book.getId())
        .bookStatus(BookStatus.BEST)
        .tradeProgress(READY)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .build();
  }

  public static Post defaultIdPost(Category category, Book book, UUID sellerId, Integer emdId) {
    return Post.builder()
        .id(1L)
        .title(book.getTitle())
        .status(ACTIVE)
        .category(category)
        .sellerId(sellerId)
        .bookId(book.getId())
        .bookStatus(BookStatus.BEST)
        .tradeProgress(READY)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .build();
  }

  public static Post customStatusPost(Category category, Book book, UUID sellerId, Integer emdId, TradeProgress progress, com.bob.domain.post.entity.status.Status status) {
    return Post.builder()
        .title(book.getTitle())
        .category(category)
        .sellerId(sellerId)
        .bookId(book.getId())
        .bookStatus(BookStatus.BEST)
        .tradeProgress(progress)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .status(status)
        .build();
  }

  public static List<Post> DEFAULT_MOCK_POSTS() {
    return List.of(
        Post.builder()
            .id(1L)
            .status(ACTIVE)
            .title("객체지향의 사실과 오해")
            .category(defaultCategory())
            .bookId(1L)
            .bookStatus(BookStatus.BEST)
            .tradeProgress(READY)
            .sellPrice(10000)
            .thumbnailUrl("http://thumbnail1.com")
            .build(),
        Post.builder()
            .id(2L)
            .status(ACTIVE)
            .title("오브젝트")
            .category(defaultCategory())
            .bookId(2L)
            .bookStatus(BookStatus.LOW)
            .tradeProgress(READY)
            .sellPrice(15000)
            .thumbnailUrl("http://thumbnail2.com")
            .build()
    );
  }
}
