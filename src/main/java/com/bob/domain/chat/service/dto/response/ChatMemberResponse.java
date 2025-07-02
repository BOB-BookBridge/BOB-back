package com.bob.domain.chat.service.dto.response;

import com.bob.domain.member.service.dto.response.MemberProfileResponse;
import java.util.UUID;

public record ChatMemberResponse(
    UUID memberId,
    String nickname,
    String profileImageUrl
) {

  public static ChatMemberResponse from(MemberProfileResponse response) {
    return new ChatMemberResponse(response.memberId(), response.nickname(), response.profileImageUrl());
  }
}
