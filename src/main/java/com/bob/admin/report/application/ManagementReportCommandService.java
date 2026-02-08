package com.bob.admin.report.application;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.bob.admin.post.domain.ManagementStatus;
import com.bob.admin.post.domain.PostManagementHistory;
import com.bob.admin.post.domain.repository.PostManagementHistoryRepository;
import com.bob.admin.report.application.dto.command.ProcessManagementReportStatusCommand;
import com.bob.admin.report.application.port.in.ManagementReportProcessor;
import com.bob.admin.report.application.port.out.ManagementReportMemberPort;
import com.bob.admin.report.application.port.out.ManagementReportPort;
import com.bob.admin.report.application.port.result.ReportedMemberInfo;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagementReportCommandService implements ManagementReportProcessor {

    private final ManagementReportPort reportPort;
    private final ManagementReportMemberPort memberPort;

    private final PostManagementHistoryRepository historyRepository;

    @Override
    public void process(Long reportId, ProcessManagementReportStatusCommand command) {
        ReportedMemberInfo reportedInfo = reportPort.changeStatus(reportId, command.managerId(), command.status());

        if ("POST".equals(reportedInfo.currentTarget()) && "PROCESSED".equals(command.status()))
            recordHistory(command, reportedInfo);

        if (reportedInfo.count() >= 3)
            memberPort.ban(reportedInfo.id(), command.memo());
        else
            memberPort.updateMemo(reportedInfo.id(), command.memo());
    }

    private void recordHistory(ProcessManagementReportStatusCommand command, ReportedMemberInfo reportedInfo) {
        historyRepository.save(PostManagementHistory.create(
            reportedInfo.currentTargetId(),
            command.managerId(),
            ManagementStatus.ACTIVE,
            ManagementStatus.BANNED,
            command.memo()
        ));
    }
}
