package com.bob.domain.member.service.dto.command;

import com.bob.domain.member.entity.Member;

public record CreateMemberCommand(
    String email,
    String password,
    String nickname,
    Integer emdId
) {

  public Member toMember(String encodedPassword) {
    return Member.builder()
        .email(email)
        .password(encodedPassword)
        .nickname(nickname)
        .build();
  }
}
