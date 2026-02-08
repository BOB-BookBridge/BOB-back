package com.bob.core.report.application.dto.command;

import java.util.UUID;

import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;

public record RegisterReportByAdminCommand(
    ReportTarget target,
    ReportStatus status,
    Long targetId,
    String reason,
    UUID managerId,
    UUID reportedId
) {

}
