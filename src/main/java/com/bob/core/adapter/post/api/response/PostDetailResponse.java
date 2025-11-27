package com.bob.core.adapter.post.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.adapter.post.api.response.internal.PostTrade;
import com.bob.core.application.post.dto.result.PostDetail;
import com.bob.core.application.post.port.result.PostFile;
import com.bob.core.application.post.port.result.PostMember;
import com.bob.core.application.post.port.result.PostMemberWishResult;

@Builder
public record PostDetailResponse(
    Long id,
    String status,
    Integer categoryId,
    String title,
    String thumbnailUrl,
    String description,
    Integer price,
    List<PostFile> images,
    WriterInfo writer,
    BookInfo book,
    PostTrade trade,
    String bookStatus,
    String tradeStatus,
    Integer scrapCount,
    Integer viewCount,
    Boolean isFavorite,
    Boolean isOwner,
    Boolean wishOnly,
    LocalDateTime createdAt
) {

    public static PostDetailResponse of(PostDetail detail, PostTrade trade) {
        return PostDetailResponse.builder()
            .id(detail.id())
            .status(detail.status())
            .categoryId(detail.categoryId())
            .title(detail.title())
            .thumbnailUrl(detail.thumbnailUrl())
            .description(detail.description())
            .price(detail.price())
            .images(detail.images())
            .writer(WriterInfo.of(detail.writer()))
            .book(BookInfo.of(detail.book()))
            .trade(trade)
            .bookStatus(detail.bookStatus())
            .tradeStatus(detail.tradeStatus())
            .scrapCount(detail.scrapCount())
            .viewCount(detail.viewCount())
            .isFavorite(detail.isFavorite())
            .isOwner(detail.isOwner())
            .wishOnly(detail.wishOnly())
            .createdAt(detail.createdAt())
            .build();
    }

    @Builder
    public record WriterInfo(
        UUID id,
        String nickname,
        Integer emdId,
        String profileImageUrl,
        List<String> interests,
        List<PostMemberWishResult> wishes
    ) {

        public static WriterInfo of(PostMember member) {
            return WriterInfo.builder()
                .id(member.id())
                .nickname(member.nickname())
                .emdId(member.emdId())
                .profileImageUrl(member.profileImageUrl())
                .interests(member.interests())
                .wishes(member.wishes())
                .build();
        }
    }

    @Builder
    public record BookInfo(String isbn, String title, String author, String description, String pubDate) {

        public static BookInfo of(PostDetail.BookInfo book) {
            return BookInfo.builder()
                .isbn(book.isbn())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pubDate(book.pubDate())
                .build();
        }
    }
}
