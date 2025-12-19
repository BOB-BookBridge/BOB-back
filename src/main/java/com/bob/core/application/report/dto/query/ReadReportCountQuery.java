package com.bob.core.application.report.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadReportCountQuery(List<UUID> reportedIds) {

}
