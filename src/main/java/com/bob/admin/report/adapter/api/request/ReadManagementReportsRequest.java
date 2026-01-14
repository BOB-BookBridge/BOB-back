package com.bob.admin.report.adapter.api.request;

import com.bob.shared.web.annotation.AllowedValues;

public record ReadManagementReportsRequest(
    String reporterEmail,
    String reportedEmail,

    @AllowedValues(
        value = {"CHAT", "POST"},
        ignoreCase = true
    )
    String type,

    @AllowedValues(
        value = {"PENDING", "IN_REVIEW", "PROCESSED", "CLOSED", "DUPLICATED"},
        ignoreCase = true
    )
    String status
) {

}
