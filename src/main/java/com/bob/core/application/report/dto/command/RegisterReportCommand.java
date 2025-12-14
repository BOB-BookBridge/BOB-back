package com.bob.core.application.report.dto.command;

import java.util.UUID;

import com.bob.core.domain.report.ReportTarget;

public record RegisterReportCommand(ReportTarget target, Long targetId, String reason, UUID reporterId, UUID reportedId) {

}
