package com.bob.core.report.application.port.in;

import static com.bob.core.report.domain.ReportStatus.DUPLICATED;
import static com.bob.core.report.domain.ReportStatus.PROCESSED;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.event.ReportPostProcessedEvent;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 수정 테스트")
@RequiredArgsConstructor
@ContainerTest
class ReportModifierTest {

    private final ReportModifier reportModifier;
    private final ReportRepository reportRepository;

    @MockitoBean
    private final ApplicationEventPublisher eventPublisher;

    @Test
    void 신고_상태_변경() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        ChangeReportStatusCommand command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));
    }

    @Test
    void 신고_처리_시_동일_대상_신고는_중복_처리() {
        Report report1 = reportRepository.save(ReportFixture.createReport(1L));
        report1.review(MANAGER_ID);

        Report report2 = reportRepository.save(ReportFixture.createReport(1L));
        Report report3 = reportRepository.save(ReportFixture.createReport(1L));

        var command = new ChangeReportStatusCommand(MANAGER_ID, "PROCESSED");

        Report result = reportModifier.changeStatus(report1.getId(), command);

        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));

        // 동일 대상 신고들은 중복 처리
        assertThat(report2.getStatus()).isEqualTo(DUPLICATED);
        assertThat(report3.getStatus()).isEqualTo(DUPLICATED);
    }
}
