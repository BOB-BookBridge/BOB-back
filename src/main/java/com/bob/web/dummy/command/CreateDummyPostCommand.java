package com.bob.web.dummy.command;

import com.bob.domain.book.entity.Book;
import com.bob.domain.category.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.status.PostStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateDummyPostCommand(
    Book book,
    Category category,
    UUID sellerId,
    int sellPrice,
    String description,
    int areaId
) {

  public Post toPost(String cover, BookStatus status) {
    return Post.builder()
        .postStatus(PostStatus.READY)
        .category(category)
        .sellerId(sellerId)
        .thumbnailUrl(cover)
        .book(book)
        .bookStatus(status)
        .sellPrice(sellPrice)
        .description(description)
        .registrationAreaId(areaId)
        .build();
  }
}
