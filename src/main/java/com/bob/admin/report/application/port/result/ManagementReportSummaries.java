package com.bob.admin.report.application.port.result;

import java.util.List;

public record ManagementReportSummaries(Long totalCount, List<ManagementReport> reports) {

}
