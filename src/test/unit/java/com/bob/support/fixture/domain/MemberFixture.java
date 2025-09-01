package com.bob.support.fixture.domain;

import static com.bob.domain.member.entity.Status.ACTIVE;
import static com.bob.domain.member.entity.Status.WITHDRAW;

import com.bob.domain.member.entity.Member;
import java.util.UUID;

public class MemberFixture {

  public static final UUID MEMBER_ID = UUID.randomUUID();
  public static final UUID OTHER_MEMBER_ID = UUID.randomUUID();

  public static Member defaultMember() {
    return Member.builder()
        .status(ACTIVE)
        .email("test@email.com")
        .password("password")
        .nickname("tester")
        .build();
  }

  public static Member defaultIdMember() {
    return Member.builder()
        .id(MEMBER_ID)
        .status(ACTIVE)
        .email("test@email.com")
        .password("password")
        .nickname("tester")
        .build();
  }

  public static Member otherMember() {
    return Member.builder()
        .status(ACTIVE)
        .email("unknown@email.com")
        .password("password")
        .nickname("anonymous")
        .build();
  }

  public static Member customEmailMember(String email) {
    return Member.builder()
        .status(ACTIVE)
        .email(email)
        .password("password")
        .nickname("tester")
        .build();
  }

  public static Member encryptPasswordMember(String encryptedPassword) {
    return Member.builder()
        .status(ACTIVE)
        .email("test@email.com")
        .password(encryptedPassword)
        .nickname("tester")
        .build();
  }

  public static Member removedMember() {
    return Member.builder()
        .status(WITHDRAW)
        .email("test@email.com")
        .password("password")
        .nickname("tester")
        .build();
  }
}
