package com.bob.support.fixture.auth.port.dto;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import com.bob.security.application.port.dto.SocialAuthMember;

public class SocialAuthMemberFixture {

    public static SocialAuthMember createSocialAuthMember(String status) {
        return SocialAuthMember.of(MEMBER_ID, status);
    }

    public static SocialAuthMember createActiveSocialAuthMember() {
        return createSocialAuthMember("ACTIVE");
    }

    public static SocialAuthMember createDeactivatedSocialAuthMember() {
        return createSocialAuthMember("DEACTIVATED");
    }

    public static SocialAuthMember createBannedSocialAuthMember() {
        return createSocialAuthMember("BANNED");
    }
}
