package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.CategoryFixture.defaultCategory;

import com.bob.domain.book.entity.Book;
import com.bob.domain.category.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.status.PostStatus;
import java.util.List;
import java.util.UUID;

public class PostFixture {

  public static Post defaultPost(Category category, Book book, UUID sellerId, Integer emdId) {
    return Post.builder()
        .book(book)
        .sellerId(sellerId)
        .category(category)
        .bookStatus(BookStatus.BEST)
        .postStatus(PostStatus.READY)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .build();
  }

  public static Post defaultIdPost(Category category, Book book, UUID sellerId, Integer emdId) {
    return Post.builder()
        .id(1L)
        .book(book)
        .sellerId(sellerId)
        .category(category)
        .bookStatus(BookStatus.BEST)
        .postStatus(PostStatus.READY)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .build();
  }

  public static Post customStatusPost(Category category, Book book, UUID sellerId, Integer emdId, PostStatus status) {
    return Post.builder()
        .book(book)
        .sellerId(sellerId)
        .category(category)
        .bookStatus(BookStatus.BEST)
        .postStatus(status)
        .sellPrice(30000)
        .description("Description")
        .registrationAreaId(emdId)
        .thumbnailUrl("https://image/1.png")
        .build();
  }

  public static List<Post> DEFAULT_MOCK_POSTS() {
    return List.of(
        Post.builder()
            .id(1L)
            .book(Book.builder().title("객체지향의 사실과 오해").build())
            .category(defaultCategory())
            .postStatus(PostStatus.READY)
            .sellPrice(10000)
            .thumbnailUrl("http://thumbnail1.com")
            .bookStatus(BookStatus.BEST)
            .build(),
        Post.builder()
            .id(2L)
            .book(Book.builder().title("오브젝트").build())
            .category(defaultCategory())
            .postStatus(PostStatus.READY)
            .sellPrice(15000)
            .thumbnailUrl("http://thumbnail2.com")
            .bookStatus(BookStatus.LOW)
            .build()
    );
  }
}
