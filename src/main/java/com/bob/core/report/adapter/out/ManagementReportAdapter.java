package com.bob.core.report.adapter.out;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.report.application.port.out.ManagementReportPort;
import com.bob.admin.report.application.port.result.ManagementReport;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.report.application.dto.result.ReportSummaries;
import com.bob.core.report.application.port.in.ReportSearcher;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;

@Component
@RequiredArgsConstructor
public class ManagementReportAdapter implements ManagementReportPort {

    private final ReportSearcher reportSearcher;

    private final MemberReader memberReader;

    @Override
    public ManagementReportSummaries readAll(
        String reporterEmail, String reportedEmail, String type, String status, Pageable pageable
    ) {
        SearchReportsQuery query = buildQuery(reporterEmail, reportedEmail, type, status);

        ReportSummaries summaries = reportSearcher.searchByQuery(query, pageable);

        List<ManagementReport> managementReports = summaries.reports().stream().map(this::convert).toList();

        return new ManagementReportSummaries(summaries.totalCount(), managementReports);
    }

    private SearchReportsQuery buildQuery(String reporterEmail, String reportedEmail, String type, String status) {
        UUID reporterId = reporterEmail != null ? getId(reporterEmail) : null;
        UUID reportedId = reportedEmail != null ? getId(reportedEmail) : null;
        ReportTarget target = type != null ? ReportTarget.valueOf(type) : null;
        ReportStatus reportStatus = status != null ? ReportStatus.valueOf(status) : null;

        return new SearchReportsQuery(reporterId, reportedId, target, reportStatus);
    }

    private ManagementReport convert(Report report) {
        String reporterEmail = getEmail(report.getReporterId());
        String reportedEmail = getEmail(report.getReportedId());
        String managerNickname = report.getManagerId() != null ? getNickname(report.getManagerId()) : null;

        return ManagementReport.builder()
            .id(report.getId())
            .status(report.getStatus().name())
            .type(report.getTarget().name())
            .reason(report.getReason())
            .reporterEmail(reporterEmail)
            .reportedEmail(reportedEmail)
            .createdAt(report.getCreatedAt())
            .processedAt(report.getProcessedAt())
            .managerNickname(managerNickname)
            .build();
    }

    private UUID getId(String email) {
        return memberReader.read(email).getId();
    }

    private String getEmail(UUID memberId) {
        return memberReader.read(memberId).getEmail();
    }

    private String getNickname(UUID memberId) {
        return memberReader.read(memberId).getNickname();
    }
}
