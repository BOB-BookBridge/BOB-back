package com.bob.core.report.application.dto.query;

import com.bob.core.report.domain.ReportTarget;

public record ReadReportsByTargetQuery(ReportTarget target, Long targetId) {

}
