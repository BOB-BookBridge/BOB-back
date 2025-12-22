package com.bob.core.member.domain;

import static com.bob.support.fixture.area.domain.AreaFixture.EMD_AREA_ID;
import static com.bob.support.fixture.member.MemberAreaFixture.createMemberArea;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 활동 지역 테스트")
class MemberAreaTest {

    @Test
    void 활동_지역_생성() {
        MemberArea memberArea = MemberArea.createArea(EMD_AREA_ID);

        assertThat(memberArea.getEmdId()).isEqualTo(EMD_AREA_ID);
        assertThat(memberArea.getAuthenticatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    void 활동_지역_인증_갱신() {
        MemberArea memberArea = MemberArea.createArea(EMD_AREA_ID);

        int changeEmdAreaId = 99;
        memberArea.updateAuthentication(changeEmdAreaId);

        assertThat(memberArea.getEmdId()).isEqualTo(changeEmdAreaId);
        assertThat(memberArea.getAuthenticatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    void 활동_지역_인증() {
        MemberArea memberArea = createMemberArea(LocalDate.now().minusWeeks(1));

        boolean isValid = memberArea.isAuthenticated();

        assertThat(isValid).isTrue();
    }

    @Test
    void 활동_지역_인증이_만료되면_false_반환() {
        MemberArea memberArea = createMemberArea(LocalDate.now().minusMonths(2));

        boolean isValid = memberArea.isAuthenticated();

        assertThat(isValid).isFalse();
    }
}
