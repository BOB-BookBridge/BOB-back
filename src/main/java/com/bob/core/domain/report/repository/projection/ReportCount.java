package com.bob.core.domain.report.repository.projection;

import java.util.UUID;

public interface ReportCount {

    UUID getReportedId();

    Integer getCount();
}
