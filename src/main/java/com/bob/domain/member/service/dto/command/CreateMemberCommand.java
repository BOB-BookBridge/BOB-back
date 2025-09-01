package com.bob.domain.member.service.dto.command;

import static com.bob.domain.member.entity.Status.ACTIVE;

import com.bob.domain.member.entity.Member;

public record CreateMemberCommand(
    String email,
    String password,
    String nickname,
    Integer emdId
) {

  public Member toMember(String encodedPassword) {
    return Member.builder()
        .status(ACTIVE)
        .email(email)
        .password(encodedPassword)
        .nickname(nickname)
        .build();
  }
}
