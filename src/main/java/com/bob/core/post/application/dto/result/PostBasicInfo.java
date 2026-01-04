package com.bob.core.post.application.dto.result;

import java.util.UUID;

import com.bob.core.post.domain.Post;

public record PostBasicInfo(
    Long id, String status, String tradeStatus, UUID sellerId, Long sellerBookId,
    String title, String thumbnailUrl, Integer price, Boolean wishOnly
) {

    public static PostBasicInfo of(Post post) {
        return new PostBasicInfo(
            post.getId(),
            post.getStatus().name(),
            post.getTradeProgress().name(),
            post.getWriterId(),
            post.getWriterBookId(),
            post.getTitle(),
            post.getThumbnailUrl(),
            post.getPrice(),
            post.isWishOnly()
        );
    }
}
