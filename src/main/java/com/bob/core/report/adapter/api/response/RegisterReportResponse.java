package com.bob.core.report.adapter.api.response;

import com.bob.core.report.domain.Report;

public record RegisterReportResponse(Long id) {

    public static RegisterReportResponse of(Report report) {
        return new RegisterReportResponse(report.getId());
    }
}
