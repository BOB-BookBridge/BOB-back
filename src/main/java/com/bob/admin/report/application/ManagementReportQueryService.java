package com.bob.admin.report.application;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.report.application.dto.query.ReadManagementReportsQuery;
import com.bob.admin.report.application.port.in.ManagementReportReader;
import com.bob.admin.report.application.port.out.ManagementReportPort;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementReportQueryService implements ManagementReportReader {

    private final ManagementReportPort reportPort;

    @Override
    public ManagementReportSummaries readAll(ReadManagementReportsQuery query, Pageable pageable) {
        return reportPort.readAll(query.reporterEmail(), query.reportedEmail(), query.type(), query.status(), pageable);
    }
}
