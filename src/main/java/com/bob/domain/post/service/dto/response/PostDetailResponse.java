package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PostDetailResponse(
    Long postId,
    UUID sellerId,
    String thumbnailUrl,
    Integer sellPrice,
    String bookStatus,
    String postStatus,
    Integer category,
    BookInfo book,
    String description,
    List<String> images,
    WriterInfo writer,
    Integer scrapCount,
    Integer viewCount,
    Boolean isFavorite,
    Boolean isOwner,
    LocalDateTime createdAt
) {

  public static PostDetailResponse from(
      Post post,
      PostMemberSummaryResponse memberSummary,
      PostAreaSummaryResponse areaSummary,
      boolean isFavorite,
      boolean isOwner
  ) {
    return PostDetailResponse.builder()
        .postId(post.getId())
        .sellerId(post.getSellerId())
        .thumbnailUrl(post.getThumbnailUrl())
        .sellPrice(post.getSellPrice())
        .bookStatus(post.getBookStatus().name())
        .postStatus(post.getPostStatus().getStatus())
        .category(post.getCategory().getId())
        .book(BookInfo.from(post))
        .description(post.getDescription())
        .images(List.of())
        .writer(WriterInfo.of(
            post.getSellerId(),
            memberSummary.nickname(), memberSummary.profileImageUrl(),
            areaSummary.emdName(), areaSummary.siggName()
        ))
        .scrapCount(post.getScrapCount())
        .viewCount(post.getViewCount())
        .isFavorite(isFavorite)
        .isOwner(isOwner)
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

    public static BookInfo from(Post post) {
      return BookInfo.builder()
          .title(post.getBook().getTitle())
          .author(post.getBook().getAuthor())
          .description(post.getBook().getDescription())
          .priceStandard(post.getBook().getPriceStandard())
          .pubDate(post.getBook().getPubDate().toString())
          .build();
    }
  }

  @Builder
  public record WriterInfo(
      UUID memberId,
      String nickname,
      String activityArea,
      String profileUrl
  ) {

    public static WriterInfo of(UUID memberId, String nickname, String profileUrl, String emdName, String siggName) {
      return WriterInfo.builder()
          .memberId(memberId)
          .nickname(nickname)
          .activityArea(String.format("%s %s", siggName, emdName))
          .profileUrl(profileUrl)
          .build();
    }
  }
}