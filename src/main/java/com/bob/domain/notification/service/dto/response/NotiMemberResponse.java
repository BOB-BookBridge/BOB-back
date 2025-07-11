package com.bob.domain.notification.service.dto.response;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public record NotiMemberResponse(
    UUID memberId,
    String nickname,
    String profile
) {

  public static NotiMemberResponse from(MemberProfileResponse response) {
    return new NotiMemberResponse(response.memberId(), response.nickname(), response.profileImageUrl());
  }
}
