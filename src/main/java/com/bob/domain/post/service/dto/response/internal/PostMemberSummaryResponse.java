package com.bob.domain.post.service.dto.response.internal;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import lombok.Builder;

@Builder
public record PostMemberSummaryResponse(
    String nickname,
    String profileImageUrl
) {

  public static PostMemberSummaryResponse from(MemberProfileResponse response) {
    return PostMemberSummaryResponse.builder()
        .nickname(response.nickname())
        .profileImageUrl(response.profileImageUrl())
        .build();
  }
}
