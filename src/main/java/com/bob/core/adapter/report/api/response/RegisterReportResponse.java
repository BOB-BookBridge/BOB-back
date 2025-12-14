package com.bob.core.adapter.report.api.response;

import com.bob.core.domain.report.Report;

public record RegisterReportResponse(Long id) {

    public static RegisterReportResponse of(Report report) {
        return new RegisterReportResponse(report.getId());
    }
}
