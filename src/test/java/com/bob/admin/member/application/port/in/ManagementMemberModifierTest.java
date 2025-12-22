package com.bob.admin.member.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.admin.member.application.dto.command.ChangeManagementMemberStatusCommand;
import com.bob.admin.member.application.port.result.ManagementMember;
import com.bob.support.annotation.ContainerTest;

@DisplayName("관리 회원 정보 변경 테스트")
@ContainerTest
record ManagementMemberModifierTest(ManagementMemberModifier memberModifier) {

    @Test
    void 상태_강제_변경() {
        var command = new ChangeManagementMemberStatusCommand(OTHER_MEMBER_ID, "BANNED", "신고 누적");

        ManagementMember managementMember = memberModifier.changeStatus(command);

        assertThat(managementMember.getStatus()).isEqualTo("BANNED");
    }
}
