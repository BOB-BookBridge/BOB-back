package com.bob.core.report.domain.repository.projection;

import java.util.UUID;

public interface ReportCount {

    UUID getReportedId();

    Integer getCount();
}
