package com.bob.domain.post.service.dto.response;

import lombok.Builder;

@Builder
public record PostMemberSummaryResponse(
    String nickname,
    String profileImageUrl
) {

  public static PostMemberSummaryResponse of(String nickname, String profileImageUrl) {
    return PostMemberSummaryResponse.builder()
        .nickname(nickname)
        .profileImageUrl(profileImageUrl)
        .build();
  }
}
