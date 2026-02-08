package com.bob.core.report.application.port.in;

import static com.bob.core.report.domain.ReportStatus.PENDING;
import static com.bob.core.report.domain.ReportStatus.PROCESSED;
import static com.bob.core.report.domain.ReportTarget.CHAT;
import static com.bob.core.report.domain.ReportTarget.POST;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.report.application.dto.command.RegisterReportByAdminCommand;
import com.bob.core.report.application.dto.command.RegisterReportCommand;
import com.bob.core.report.domain.Report;
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

    @Test
    void 관리자_신고_등록() {
        var command = new RegisterReportByAdminCommand(POST, PROCESSED, 1L, "관리자 기능", MANAGER_ID, OTHER_MEMBER_ID);

        Integer result = reportRegister.registerByManager(command);

        assertThat(result).isEqualTo(1);
    }
}
