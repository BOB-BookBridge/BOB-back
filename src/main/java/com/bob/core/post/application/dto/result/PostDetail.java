package com.bob.core.post.application.dto.result;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

import com.bob.core.post.application.port.result.PostBook;
import com.bob.core.post.application.port.result.PostFile;
import com.bob.core.post.application.port.result.PostMember;
import com.bob.core.post.domain.Post;

@Builder
public record PostDetail(
    Long id,

    String status, Integer categoryId, String title, String thumbnailUrl, String description, Integer price,

    List<PostFile> images, List<String> filterWords,

    PostMember writer, Long writerBookId,

    BookInfo book,

    String bookStatus, String tradeStatus,

    Integer registrationAreaId, Integer scrapCount, Integer viewCount,

    Boolean isFavorite, Boolean isOwner, Boolean wishOnly,

    LocalDateTime createdAt
) {

    public static PostDetail of(
        Post post, PostBook book, PostMember member, List<PostFile> files,
        boolean isFavorite, boolean isOwner
    ) {
        return PostDetail.builder()
            .id(post.getId())
            .status(post.getStatus().name())
            .categoryId(post.getCategoryId())
            .title(post.getTitle())
            .thumbnailUrl(post.getThumbnailUrl())
            .description(post.getDescription())
            .price(post.getPrice())
            .images(files)
            .filterWords(List.copyOf(post.getFilterWords()))
            .writer(member)
            .writerBookId(post.getWriterBookId())
            .book(BookInfo.of(book))
            .bookStatus(post.getBookStatus().name())
            .tradeStatus(post.getTradeProgress().name())
            .scrapCount(post.getScrapCount())
            .viewCount(post.getViewCount())
            .registrationAreaId(post.getRegistrationAreaId())
            .isFavorite(isFavorite)
            .isOwner(isOwner)
            .wishOnly(post.isWishOnly())
            .createdAt(post.getCreatedAt())
            .build();
    }

    @Builder
    public record BookInfo(
        String isbn,
        String title,
        String author,
        String description,
        String pubDate
    ) {

        public static BookInfo of(PostBook book) {
            return BookInfo.builder()
                .isbn(book.isbn())
                .title(book.title())
                .author(book.author())
                .description(book.description())
                .pubDate(book.pubDate().toString())
                .build();
        }
    }
}
