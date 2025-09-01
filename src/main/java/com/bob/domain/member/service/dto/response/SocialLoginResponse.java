package com.bob.domain.member.service.dto.response;

import com.bob.domain.member.entity.Member;
import java.util.UUID;
import lombok.Builder;

@Builder
public record SocialLoginResponse(
    UUID memberId,
    String status
) {

  public static SocialLoginResponse from(Member member) {
    return SocialLoginResponse.builder()
        .memberId(member.getId())
        .status(member.getStatus().name())
        .build();
  }
}
