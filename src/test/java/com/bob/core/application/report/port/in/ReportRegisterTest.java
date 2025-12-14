package com.bob.core.application.report.port.in;

import static com.bob.core.domain.report.ReportStatus.PENDING;
import static com.bob.core.domain.report.ReportTarget.CHAT;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.report.dto.command.RegisterReportCommand;
import com.bob.core.domain.report.Report;
import com.bob.support.annotation.ContainerTest;

@DisplayName("신고 등록 테스트")
@ContainerTest
record ReportRegisterTest(ReportRegister reportRegister) {

    @Test
    void 신고_등록() {
        RegisterReportCommand command = new RegisterReportCommand(CHAT, 1L, "욕설/비방", MEMBER_ID, OTHER_MEMBER_ID);

        Report report = reportRegister.register(command);

        assertThat(report.getId()).isNotNull();
        assertThat(report.getStatus()).isEqualTo(PENDING);
    }
}
