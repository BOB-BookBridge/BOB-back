package com.bob.core.application.report;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.report.dto.command.RegisterReportCommand;
import com.bob.core.application.report.port.in.ReportRegister;
import com.bob.core.domain.report.Report;
import com.bob.core.domain.report.repository.ReportRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService implements ReportRegister {

    private final ReportRepository reportRepository;

    @Override
    public Report register(RegisterReportCommand command) {
        Report report = Report.createReport(command.target(), command.targetId(), command.reason(),
            command.reporterId(), command.reportedId());

        return reportRepository.save(report);
    }
}
