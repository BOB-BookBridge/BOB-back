package com.bob.core.member.application.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.member.application.port.result.MemberBookcaseResult;
import com.bob.core.member.domain.SocialProvider;

@Builder
public record MemberDetail(
    UUID id,
    String status,
    String role,
    String email,
    String nickname,
    String profileImageUrl,
    MemberAreaDetail area,
    List<String> interests,
    List<MemberBookcaseResult> bookcase,
    List<MemberWishDetail> wishes,
    String memo,
    boolean isSocial,
    LocalDateTime lastActiveAt,
    LocalDateTime createdAt
) {

    public static MemberDetail deactivateMemberDetail(UUID id, MemberAreaDetail area, SocialProvider provider) {
        return MemberDetail.builder()
            .id(id)
            .status("DEACTIVATED")
            .role("USER")
            .email("delete")
            .nickname("(알 수 없음)")
            .profileImageUrl(null)
            .interests(List.of())
            .area(area)
            .bookcase(List.of())
            .wishes(List.of())
            .isSocial(provider != null)
            .lastActiveAt(null)
            .createdAt(null)
            .build();
    }
}


