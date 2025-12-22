package com.bob.core.member.adapter.api.response;

import lombok.Builder;

import com.bob.core.member.application.dto.result.MemberWishDetail;

@Builder
public record MemberWishResponse(
    Long id, String title, String author, String cover
) {

    public static MemberWishResponse of(MemberWishDetail wish) {
        return MemberWishResponse.builder()
            .id(wish.id())
            .title(wish.title())
            .author(wish.author())
            .cover(wish.cover())
            .build();
    }
}
