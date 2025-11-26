package com.bob.core.domain.member;

import static com.bob.core.domain.member.MemberInterest.createMemberInterest;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 관심사 테스트")
class MemberInterestTest {

    @Test
    void 회원_관심사_생성() {
        Long interestId = 1L;
        String displayName = "Java";

        MemberInterest memberInterest = createMemberInterest(interestId, displayName);

        assertThat(memberInterest.getInterestId()).isEqualTo(interestId);
        assertThat(memberInterest.getDisplayName()).isEqualTo("Java");
    }

    @Test
    void 회원_관심사_이름_표현() {
        Long interestId = 99L;
        String displayName = "  Java  ";

        MemberInterest interest = createMemberInterest(interestId, displayName);

        assertThat(interest.getDisplayName()).isEqualTo("  Java  ");
    }
}
