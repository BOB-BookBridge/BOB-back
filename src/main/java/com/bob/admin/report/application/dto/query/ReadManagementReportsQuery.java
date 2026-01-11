package com.bob.admin.report.application.dto.query;

public record ReadManagementReportsQuery(String reporterEmail, String reportedEmail, String type, String status) {

    public static ReadManagementReportsQuery of(String reporterEmail, String reportedEmail,
        String type, String status
    ) {
        if (type != null)
            type = type.toUpperCase();

        if (status != null)
            status = status.toUpperCase();

        return new ReadManagementReportsQuery(reporterEmail, reportedEmail, type, status);
    }
}
