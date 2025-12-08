package com.bob.security.model;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberDetailsTest {

    MemberDetails memberDetails = new MemberDetails(MEMBER_ID, "test@example.com", "password", "USER", true);

    @Test
    void 패스워드_조회() {
        assertThat(memberDetails.getPassword()).isEqualTo("password");
    }

    @Test
    void 이름_조회() {
        assertThat(memberDetails.getUsername()).isEqualTo("test@example.com");
    }

    @Test
    void 역할_조회() {
        assertThat(memberDetails.getAuthorities())
            .extracting("authority")
            .contains("ROLE_USER");
    }

    @Test
    void 활성_여부_조회() {
        assertThat(memberDetails.isEnabled()).isTrue();
    }
}
