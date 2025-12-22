package com.bob.integration.adapter.management;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.management.application.port.out.ManagementReportPort;
import com.bob.core.management.application.port.result.ManagementMemberReport;
import com.bob.core.report.application.dto.query.ReadReportCountQuery;
import com.bob.core.report.application.dto.query.ReadReportQuery;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.projection.ReportCount;

@Component
@RequiredArgsConstructor
public class ManagementReportAdapter implements ManagementReportPort {

    private final ReportReader reportReader;

    @Override
    public ManagementMemberReport read(UUID reportedId) {
        ReadReportQuery query = new ReadReportQuery(reportedId);

        List<Report> reports = reportReader.read(query);

        Map<ReportTarget, List<Report>> reportsByTarget = reports.stream()
            .filter(report -> report.getStatus() == ReportStatus.PROCESSED)
            .collect(Collectors.groupingBy(Report::getTarget));

        List<Report> chatReports = reportsByTarget.getOrDefault(ReportTarget.CHAT, List.of());
        List<Report> postReports = reportsByTarget.getOrDefault(ReportTarget.POST, List.of());

        return ManagementMemberReport.of(
            chatReports.size(), chatReports.stream().map(Report::getReason).collect(Collectors.toSet()),
            chatReports.stream().map(Report::getTargetId).toList(),

            postReports.size(), postReports.stream().map(Report::getReason).collect(Collectors.toSet()),
            postReports.stream().map(Report::getTargetId).toList()
        );
    }

    @Override
    public Map<UUID, Integer> readCounts(List<UUID> reportedIds) {
        ReadReportCountQuery query = new ReadReportCountQuery(reportedIds);

        return reportReader.readProcessedReportCounts(query).stream()
            .collect(Collectors.toMap(ReportCount::getReportedId, ReportCount::getCount));
    }
}
