package com.bob.core.application.report.port.in;

import com.bob.core.application.report.dto.command.RegisterReportCommand;
import com.bob.core.domain.report.Report;

public interface ReportRegister {

    Report register(RegisterReportCommand command);
}
