package com.bob.core.report.application;

import static com.bob.core.report.domain.ReportTarget.POST;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.application.dto.command.RegisterReportByAdminCommand;
import com.bob.core.report.application.dto.command.RegisterReportCommand;
import com.bob.core.report.application.port.in.ReportModifier;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.application.port.in.ReportRegister;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.domain.repository.projection.ReportCount;
import com.bob.core.report.event.ReportPostProcessedEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService implements ReportRegister, ReportModifier {

    private final ReportRepository reportRepository;
    private final ReportReader reportReader;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Report register(RegisterReportCommand command) {
        Report report = Report.createReport(command.target(), command.targetId(), command.reason(),
            command.reporterId(), command.reportedId());

        return reportRepository.save(report);
    }

    @Override
    public Integer registerByManager(RegisterReportByAdminCommand command) {
        reportRepository.save(Report.createProcessedReport(
            command.target(),
            command.targetId(),
            command.reason(),
            command.managerId(),
            command.reportedId()
        ));

        ReportCount history = reportRepository.countProcessedByReportedIds(List.of(command.reportedId())).get(0);

        return history.getCount();
    }

    @Override
    public Report changeStatus(Long reportId, ChangeReportStatusCommand command) {
        Report report = reportReader.read(reportId);

        ReportStatus status = ReportStatus.valueOf(command.status());

        switch (status) {
            case IN_REVIEW -> report.review(command.managerId());
            case PROCESSED -> {
                report.process();
                if (report.getTarget() == POST) {
                    duplicateRelatedReports(report);
                    eventPublisher.publishEvent(new ReportPostProcessedEvent(report.getTargetId()));
                }
            }
            default -> report.abort(status);
        }

        return report;
    }

    private void duplicateRelatedReports(Report report) {
        reportRepository.findAllByTargetAndTargetIdAndIdNot(report.getTarget(), report.getTargetId(), report.getId())
            .forEach(r -> r.abort(ReportStatus.DUPLICATED));
    }
}
