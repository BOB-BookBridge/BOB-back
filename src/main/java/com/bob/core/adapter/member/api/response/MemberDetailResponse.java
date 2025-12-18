package com.bob.core.adapter.member.api.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.application.member.dto.result.MemberDetail;
import com.bob.core.application.member.port.result.MemberBookcaseResult;

@Builder
public record MemberDetailResponse(
    UUID id,
    String role,
    String email,
    String nickname,
    String profileImageUrl,
    MemberAreaResponse area,
    List<String> interests,
    List<MemberBookcaseResult> bookcase,
    List<MemberWishResponse> wishes,
    boolean isSocial
) {

    public static MemberDetailResponse of(MemberDetail detail) {
        return MemberDetailResponse.builder()
            .id(detail.id())
            .role(detail.role())
            .email(detail.email())
            .nickname(detail.nickname())
            .profileImageUrl(detail.profileImageUrl())
            .area(MemberAreaResponse.of(detail.area()))
            .interests(detail.interests())
            .bookcase(detail.bookcase())
            .wishes(detail.wishes().stream()
                .map(MemberWishResponse::of)
                .toList())
            .isSocial(detail.isSocial())
            .build();
    }
}
