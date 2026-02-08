package com.bob.admin.report.application.port.in;

import static com.bob.core.report.domain.ReportStatus.CLOSED;
import static com.bob.core.report.domain.ReportStatus.DUPLICATED;
import static com.bob.core.report.domain.ReportStatus.IN_REVIEW;
import static com.bob.core.report.domain.ReportStatus.PROCESSED;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.admin.report.application.dto.command.ProcessManagementReportStatusCommand;
import com.bob.core.member.event.MemberDeactivatedEvent;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.event.ReportPostProcessedEvent;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("관리자 - 신고 처리 테스트")
@RequiredArgsConstructor
@ContainerTest
class ManagementReportProcessorTest {

    private final ManagementReportProcessor reportProcessor;
    private final ReportRepository reportRepository;
    private final EntityManager em;

    @MockitoBean
    private final ApplicationEventPublisher eventPublisher;

    @Test
    void 신고_검토_처리() {
        Report report = reportRepository.save(ReportFixture.createReport());
        var command = new ProcessManagementReportStatusCommand(MANAGER_ID, "IN_REVIEW", null);

        reportProcessor.process(report.getId(), command);

        invalidPersistenceContext();

        Report result = reportRepository.findById(report.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(IN_REVIEW);
        assertThat(result.getProcessedAt()).isNull();
    }

    @Test
    void 신고_완료_처리() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        var command = new ProcessManagementReportStatusCommand(MANAGER_ID, "PROCESSED", "메모");

        reportProcessor.process(report.getId(), command);

        invalidPersistenceContext();

        Report result = reportRepository.findById(report.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));
    }

    @Test
    void 신고_완료_처리_시_누적_신고횟수가_3회_이상이면_회원_비활성_이벤트_발행() {
        reportRepository.save(ReportFixture.createProcessedReport(99L)); // 누적 1회
        reportRepository.save(ReportFixture.createProcessedReport(100L)); // 누적 2회

        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        var command = new ProcessManagementReportStatusCommand(MANAGER_ID, "PROCESSED", "메모");

        reportProcessor.process(report.getId(), command);

        invalidPersistenceContext();

        Report result = reportRepository.findById(report.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(PROCESSED);
        assertThat(result.getProcessedAt()).isNotNull();

        then(eventPublisher).should().publishEvent(any(ReportPostProcessedEvent.class));
        then(eventPublisher).should().publishEvent(any(MemberDeactivatedEvent.class));
    }

    @Test
    void 신고_취소_처리() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        var command = new ProcessManagementReportStatusCommand(MANAGER_ID, "CLOSED", "허위");

        reportProcessor.process(report.getId(), command);

        invalidPersistenceContext();

        Report result = reportRepository.findById(report.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(CLOSED);
        assertThat(result.getProcessedAt()).isNotNull();
    }

    @Test
    void 신고_중복_처리() {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());
        var command = new ProcessManagementReportStatusCommand(MANAGER_ID, "DUPLICATED", "중복");

        reportProcessor.process(report.getId(), command);

        invalidPersistenceContext();

        Report result = reportRepository.findById(report.getId()).orElseThrow();
        assertThat(result.getManagerId()).isEqualTo(MANAGER_ID);
        assertThat(result.getStatus()).isEqualTo(DUPLICATED);
        assertThat(result.getProcessedAt()).isNotNull();
    }

    private void invalidPersistenceContext() {
        em.flush();
        em.clear();
    }
}
