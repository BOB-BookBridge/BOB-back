package com.bob.core.report.application.dto.command;

import java.util.UUID;

public record ChangeReportStatusCommand(UUID managerId, String status) {

}
