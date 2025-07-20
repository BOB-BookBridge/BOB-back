package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import lombok.Builder;

@Builder
public record TradeMemberSummary(
    String nickname,
    String profile
) {

  public static TradeMemberSummary from(MemberProfileResponse response) {
    return TradeMemberSummary.builder()
        .nickname(response.nickname())
        .profile(response.profileImageUrl())
        .build();
  }
}
