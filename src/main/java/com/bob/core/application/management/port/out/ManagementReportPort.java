package com.bob.core.application.management.port.out;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ManagementReportPort {

    Map<UUID, Integer> readCounts(List<UUID> reportedIds);
}
