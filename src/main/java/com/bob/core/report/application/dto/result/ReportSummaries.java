package com.bob.core.report.application.dto.result;

import java.util.List;

import com.bob.core.report.domain.Report;

public record ReportSummaries(Long totalCount, List<Report> reports) {

}
