package com.bob.admin.report.application.dto.command;

import java.util.UUID;

public record ProcessManagementReportStatusCommand(UUID managerId, String status, String memo) {

}
