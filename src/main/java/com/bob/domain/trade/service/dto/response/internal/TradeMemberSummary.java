package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradeMemberSummary(
    UUID id,
    String nickname,
    String profile
) {

  public static TradeMemberSummary from(MemberProfileResponse response) {
    return TradeMemberSummary.builder()
        .id(response.memberId())
        .nickname(response.nickname())
        .profile(response.profileImageUrl())
        .build();
  }
}
