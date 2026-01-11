package com.bob.admin.report.adapter.api;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.report.adapter.api.request.ReadManagementReportsRequest;
import com.bob.admin.report.application.dto.query.ReadManagementReportsQuery;
import com.bob.admin.report.application.port.in.ManagementReportReader;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;

@RestController
@RequestMapping("/management/reports")
@RequiredArgsConstructor
public class ManagementReportApi {

    private final ManagementReportReader reportReader;

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
}
