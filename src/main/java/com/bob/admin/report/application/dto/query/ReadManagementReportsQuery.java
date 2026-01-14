package com.bob.admin.report.application.dto.query;

public record ReadManagementReportsQuery(String reporterEmail, String reportedEmail, String type, String status) {

    public static ReadManagementReportsQuery of(String reporterEmail, String reportedEmail,
        String type, String status
    ) {
        return new ReadManagementReportsQuery(
            emptyToNull(reporterEmail),
            emptyToNull(reportedEmail),
            toUpperCaseOrNull(type),
            toUpperCaseOrNull(status)
        );
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    private static String toUpperCaseOrNull(String value) {
        return (value == null || value.isEmpty()) ? null : value.toUpperCase();
    }
}
