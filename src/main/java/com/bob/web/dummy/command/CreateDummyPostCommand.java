package com.bob.web.dummy.command;

import com.bob.domain.book.entity.Book;
import com.bob.domain.post.entity.status.BookStatus;
import com.bob.domain.post.entity.Category;
import com.bob.domain.post.entity.Post;
import com.bob.domain.post.entity.status.TradeProgress;
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
        .tradeProgress(TradeProgress.READY)
        .category(category)
        .sellerId(sellerId)
        .thumbnailUrl(cover)
        .bookId(book.getId())
        .bookStatus(status)
        .sellPrice(sellPrice)
        .description(description)
        .registrationAreaId(areaId)
        .build();
  }
}
