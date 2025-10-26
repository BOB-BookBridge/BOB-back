package com.bob.domain.post.service.dto.response.internal;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record PostMemberSummaryResponse(
    String nickname,
    String profileImageUrl,
    List<String> interests
) {

  public static PostMemberSummaryResponse from(MemberProfileResponse response) {
    return PostMemberSummaryResponse.builder()
        .nickname(response.nickname())
        .profileImageUrl(response.profileImageUrl())
        .interests(response.interests())
        .build();
  }
}
