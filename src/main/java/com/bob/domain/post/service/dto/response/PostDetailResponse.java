package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.service.dto.response.internal.PostBookSummaryResponse;
import com.bob.domain.post.service.dto.response.internal.PostFileSummaryResponse;
import com.bob.domain.post.service.dto.response.internal.PostFileSummaryResponse.PostFileSummary;
import com.bob.domain.post.service.port.view.PostMemberView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PostDetailResponse(
    Long postId,
    String status,
    UUID sellerId,
    Long sellerBookId,
    String thumbnailUrl,
    Integer sellPrice,
    String bookStatus,
    String postStatus,
    Integer category,
    BookInfo book,
    String description,
    List<PostFileSummary> images,
    PostMemberView writer,
    Integer scrapCount,
    Integer viewCount,
    Boolean isFavorite,
    Boolean isOwner,
    Boolean wishOnly,
    LocalDateTime createdAt
) {

  public static PostDetailResponse from(
      Post post,
      PostBookSummaryResponse bookSummary,
      PostMemberView member,
      PostFileSummaryResponse fileSummary,
      boolean isFavorite,
      boolean isOwner
  ) {
    return PostDetailResponse.builder()
        .postId(post.getId())
        .status(post.getStatus().name())
        .sellerId(post.getSellerId())
        .sellerBookId(post.getSellerBookId())
        .thumbnailUrl(post.getThumbnailUrl())
        .sellPrice(post.getSellPrice())
        .bookStatus(post.getBookStatus().name())
        .postStatus(post.getTradeProgress().name())
        .category(post.getCategory().getId())
        .book(BookInfo.from(bookSummary))
        .description(post.getDescription())
        .images(fileSummary.images())
        .writer(member)
        .scrapCount(post.getScrapCount())
        .viewCount(post.getViewCount())
        .isFavorite(isFavorite)
        .isOwner(isOwner)
        .wishOnly(post.isWishOnly())
        .createdAt(post.getCreatedAt())
        .build();
  }

  @Builder
  public record BookInfo(
      String title,
      String author,
      String description,
      Integer priceStandard,
      String pubDate
  ) {

    public static BookInfo from(PostBookSummaryResponse response) {
      return BookInfo.builder()
          .title(response.title())
          .author(response.author())
          .description(response.description())
          .priceStandard(response.priceStandard())
          .pubDate(response.pubDate().toString())
          .build();
    }
  }
}
