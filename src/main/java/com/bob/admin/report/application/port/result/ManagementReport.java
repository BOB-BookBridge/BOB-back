package com.bob.admin.report.application.port.result;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ManagementReport(
    Long id,
    String status,
    String type,
    String reason,
    ReportMemberInfo reporter,
    ReportMemberInfo reported,
    String managerNickname,
    LocalDateTime processedAt,
    LocalDateTime createdAt
) {

}
