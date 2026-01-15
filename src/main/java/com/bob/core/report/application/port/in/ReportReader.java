package com.bob.core.report.application.port.in;

import java.util.List;

import com.bob.core.report.application.dto.query.ReadReportCountQuery;
import com.bob.core.report.application.dto.query.ReadReportQuery;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.projection.ReportCount;

public interface ReportReader {

    Report read(Long reportId);

    List<Report> read(ReadReportQuery query);

    List<ReportCount> readProcessedReportCounts(ReadReportCountQuery query);
}
