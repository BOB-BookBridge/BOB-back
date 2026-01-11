package com.bob.core.report.domain.repository.dsl.query;

import java.util.UUID;

import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;

public record SearchReportsQuery(
    UUID reporterId,
    UUID reportedId,
    ReportTarget target,
    ReportStatus status
) {

}
