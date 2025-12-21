package com.bob.core.application.report.port.in;

import java.util.List;

import com.bob.core.application.report.dto.query.ReadReportCountQuery;
import com.bob.core.application.report.dto.query.ReadReportQuery;
import com.bob.core.domain.report.Report;
import com.bob.core.domain.report.repository.projection.ReportCount;

public interface ReportReader {

    List<Report> read(ReadReportQuery query);

    List<ReportCount> readProcessedReportCounts(ReadReportCountQuery query);
}
