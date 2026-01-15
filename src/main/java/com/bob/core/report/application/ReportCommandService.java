package com.bob.core.report.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.application.dto.command.RegisterReportCommand;
import com.bob.core.report.application.port.in.ReportModifier;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.application.port.in.ReportRegister;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.repository.ReportRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService implements ReportRegister, ReportModifier {

    private final ReportRepository reportRepository;
    private final ReportReader reportReader;

    @Override
    public Report register(RegisterReportCommand command) {
        Report report = Report.createReport(command.target(), command.targetId(), command.reason(),
            command.reporterId(), command.reportedId());

        return reportRepository.save(report);
    }

    @Override
    public Report changeStatus(Long reportId, ChangeReportStatusCommand command) {
        Report report = reportReader.read(reportId);

        ReportStatus status = ReportStatus.valueOf(command.status());

        switch(status) {
            case IN_REVIEW -> report.review(command.managerId());
            case PROCESSED -> report.process();
            default -> report.abort(status);
        }

        return report;
    }
}
