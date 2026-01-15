package com.bob.admin.report.application.port.out;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.admin.report.application.port.result.ManagementReportDetail;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.admin.report.application.port.result.ReportedMemberInfo;

public interface ManagementReportPort {

    ManagementReportSummaries readAll(
        String reporterEmail,
        String reportedEmail,
        String type,
        String status,
        Pageable pageable
    );

    ManagementReportDetail readDetail(Long reportId);

    ReportedMemberInfo changeStatus(Long reportId, UUID managerId, String status);
}
