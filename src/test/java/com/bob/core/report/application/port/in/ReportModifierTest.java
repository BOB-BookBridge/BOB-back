package com.bob.core.report.application.port.in;

import static com.bob.core.report.domain.ReportStatus.PROCESSED;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@ContainerTest
record ReportModifierTest(ReportModifier reportModifier, ReportRepository reportRepository) {

    @Test
    void 신고_상태_변경() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        ChangeReportStatusCommand command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();
    }
}
