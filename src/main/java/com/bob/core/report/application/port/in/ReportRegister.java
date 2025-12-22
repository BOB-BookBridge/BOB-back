package com.bob.core.report.application.port.in;

import com.bob.core.report.application.dto.command.RegisterReportCommand;
import com.bob.core.report.domain.Report;

public interface ReportRegister {

    Report register(RegisterReportCommand command);
}
