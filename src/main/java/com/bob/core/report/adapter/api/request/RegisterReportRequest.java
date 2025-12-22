package com.bob.core.report.adapter.api.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RegisterReportRequest(
    @NotNull(message = "신고자 ID는 필수입니다")
    UUID reportedId,

    @NotNull(message = "사유는 필수입니다")
    String reason
) {

}
