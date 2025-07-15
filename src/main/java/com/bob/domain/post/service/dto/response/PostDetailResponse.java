package com.bob.domain.post.service.dto.response;

import com.bob.domain.post.entity.Post;
import com.bob.domain.post.service.dto.response.PostFileSummaryResponse.PostFileSummary;
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
    List<PostFileSummary> images,
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
      PostFileSummaryResponse fileSummary,
      boolean isFavorite,
      boolean isOwner
  ) {
    return PostDetailResponse.builder()
        .postId(post.getId())
        .sellerId(post.getSellerId())
        .thumbnailUrl(post.getThumbnailUrl())
        .sellPrice(post.getSellPrice())
        .bookStatus(post.getBookStatus().name())
        .postStatus(post.getPostStatus().name())
        .category(post.getCategory().getId())
        .book(BookInfo.from(post))
        .description(post.getDescription())
        .images(fileSummary.images())
        .writer(WriterInfo.of(
            post.getSellerId(), memberSummary.nickname(), post.getRegistrationAreaId(), memberSummary.profileImageUrl()
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
      Integer emdId,
      String profileUrl
  ) {

    public static WriterInfo of(UUID memberId, String nickname, Integer registrationAreaId, String profileUrl) {
      return WriterInfo.builder()
          .memberId(memberId)
          .nickname(nickname)
          .emdId(registrationAreaId)
          .profileUrl(profileUrl)
          .build();
    }
  }
}