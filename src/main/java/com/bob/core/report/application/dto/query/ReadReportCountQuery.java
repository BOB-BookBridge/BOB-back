package com.bob.core.report.application.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadReportCountQuery(List<UUID> reportedIds) {

}
