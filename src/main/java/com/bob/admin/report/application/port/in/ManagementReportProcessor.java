package com.bob.admin.report.application.port.in;

import com.bob.admin.report.application.dto.command.ProcessManagementReportStatusCommand;

public interface ManagementReportProcessor {

    void process(Long reportId, ProcessManagementReportStatusCommand command);
}
