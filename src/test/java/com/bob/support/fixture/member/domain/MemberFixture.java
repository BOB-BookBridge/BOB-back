package com.bob.support.fixture.member.domain;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;

import java.util.UUID;

import com.bob.core.member.domain.Member;

public class MemberFixture {

    public static final UUID MEMBER_ID = UUID.fromString("0199f8c2-30ed-7ee3-a757-16196412518c");
    public static final UUID OTHER_MEMBER_ID = UUID.fromString("019a6928-6f73-79f7-bd1b-6bfd4771a302");
    public static final UUID MOCK_MEMBER_ID = UUID.fromString("019a6928-6f73-79f7-bd1b-6bfd4771a303");
    public static final UUID MANAGER_ID = UUID.fromString("021a6930-6f73-14c6-cf0a-2fd17132f102");

    public static Member createMember(String email, String password, String nickname, int emdId) {
        return Member.createMember(email, password, nickname, emdId);
    }

    public static Member createMember(String email) {
        return createMember(email, "password", "tester", EMD_AREA_ID);
    }

    public static Member createMember() {
        return createMember("test@email.com", "password", "tester", EMD_AREA_ID);
    }

    public static Member createCustomPasswordMember(String password) {
        return createMember("test@email.com", password, "tester", EMD_AREA_ID);
    }

    public static Member createOtherMember() {
        return createMember("unknown@email.com", "password", "anonymous", EMD_AREA_ID);
    }

    public static Member createSocialMember() {
        return Member.createSocialMember("test@google.com", "GOOGLE", "tester");
    }

    public static Member createUnauthenticatedMember() {
        return Member.createSocialMember("unauth@email.com", "GOOGLE", "tester");
    }
}
