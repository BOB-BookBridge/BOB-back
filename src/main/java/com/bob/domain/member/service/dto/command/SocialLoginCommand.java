package com.bob.domain.member.service.dto.command;

import com.bob.domain.member.entity.Member;
import com.bob.domain.member.entity.SocialProvider;
import lombok.Builder;

@Builder
public record SocialLoginCommand(
    String provider,
    String email,
    String nickname,
    Integer emdId
) {

  public static SocialLoginCommand of(String provider, String email, String nickname) {
    return SocialLoginCommand.builder()
        .provider(provider)
        .email(email)
        .nickname(nickname)
        .emdId(213)
        .build();
  }

  public Member toMember(String encodedPassword) {
    return Member.builder()
        .provider(SocialProvider.valueOf(provider))
        .email(email)
        .password(encodedPassword)
        .nickname(nickname)
        .build();
  }
}
