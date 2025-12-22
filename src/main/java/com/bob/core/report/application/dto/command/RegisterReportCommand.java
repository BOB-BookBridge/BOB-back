package com.bob.core.report.application.dto.command;

import java.util.UUID;

import com.bob.core.report.domain.ReportTarget;

public record RegisterReportCommand(ReportTarget target, Long targetId, String reason, UUID reporterId, UUID reportedId) {

}
