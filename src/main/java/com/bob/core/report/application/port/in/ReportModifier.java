package com.bob.core.report.application.port.in;

import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.domain.Report;

public interface ReportModifier {

    Report changeStatus(Long reportId, ChangeReportStatusCommand command);
}
