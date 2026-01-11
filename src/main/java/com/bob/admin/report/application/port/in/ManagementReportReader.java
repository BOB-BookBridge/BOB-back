package com.bob.admin.report.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.admin.report.application.dto.query.ReadManagementReportsQuery;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;

public interface ManagementReportReader {

    ManagementReportSummaries readAll(ReadManagementReportsQuery query, Pageable pageable);
}
