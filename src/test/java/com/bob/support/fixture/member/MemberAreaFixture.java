package com.bob.support.fixture.member;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;

import java.time.LocalDate;

import com.bob.core.domain.member.MemberArea;

public class MemberAreaFixture {

    public static MemberArea createMemberArea() {
        return createMemberArea(LocalDate.now());
    }

    public static MemberArea createMemberArea(LocalDate date) {
        return MemberArea.builder()
            .emdId(EMD_AREA_ID)
            .authenticatedAt(date)
            .build();
    }
}
