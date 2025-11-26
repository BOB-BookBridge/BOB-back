package com.bob.support.fixture.auth.port.dto;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;

import com.bob.security.application.port.dto.AuthMember;

public class AuthMemberFixture {

    public static AuthMember createAuthMember(String status) {
        return AuthMember.of(MEMBER_ID, "test@email.com", "password", status);
    }

    public static AuthMember createActiveAuthMember() {
        return createAuthMember("ACTIVE");
    }

    public static AuthMember createDeactivatedAuthMember() {
        return createAuthMember("DEACTIVATED");
    }
}
