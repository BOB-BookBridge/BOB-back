package com.bob.admin.report.adapter.api;

import static com.bob.shared.web.response.ResponseSymbol.UPDATED;

import java.util.UUID;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.report.adapter.api.request.ProcessManagementReportStatusRequest;
import com.bob.admin.report.adapter.api.request.ReadManagementReportsRequest;
import com.bob.admin.report.application.dto.command.ProcessManagementReportStatusCommand;
import com.bob.admin.report.application.dto.query.ReadManagementReportsQuery;
import com.bob.admin.report.application.port.in.ManagementReportProcessor;
import com.bob.admin.report.application.port.in.ManagementReportReader;
import com.bob.admin.report.application.port.result.ManagementReportDetail;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.shared.web.annotation.AuthenticationId;
import com.bob.shared.web.response.CommonResponse;
import com.bob.shared.web.response.ResponseSymbol;

@RestController
@RequestMapping("/management/reports")
@RequiredArgsConstructor
public class ManagementReportApi {

    private final ManagementReportReader reportReader;
    private final ManagementReportProcessor reportProcessor;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementReportSummaries readReports(
        @Valid ReadManagementReportsRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        ReadManagementReportsQuery query = ReadManagementReportsQuery.of(
            request.reporterEmail(),
            request.reportedEmail(),
            request.type(),
            request.status()
        );

        return reportReader.readAll(query, pageable);
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementReportDetail readReportDetail(@PathVariable Long reportId) {
        return reportReader.readDetail(reportId);
    }

    @PatchMapping("/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<ResponseSymbol> processReport(
        @AuthenticationId UUID managerId,
        @PathVariable Long reportId,
        @Valid @RequestBody ProcessManagementReportStatusRequest request
    ) {
        ProcessManagementReportStatusCommand command = new ProcessManagementReportStatusCommand(
            managerId,
            request.status(),
            request.memo()
        );

        reportProcessor.process(reportId, command);

        return new CommonResponse<>(true, UPDATED);
    }
}
