package com.bob.support.fixture.response.oauth;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.member.service.dto.response.SocialLoginResponse;

public class SocialLoginResponseFixture {

  public static final SocialLoginResponse WITHDRAWN_MEMBER_RESPONSE = SocialLoginResponse.builder()
      .memberId(MEMBER_ID)
      .status("WITHDRAW")
      .build();

  public static final SocialLoginResponse BANNED_MEMBER_RESPONSE = SocialLoginResponse.builder()
      .memberId(MEMBER_ID)
      .status("BANNED")
      .build();
}
