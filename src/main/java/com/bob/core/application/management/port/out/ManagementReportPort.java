package com.bob.core.application.management.port.out;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bob.core.application.management.port.result.ManagementMemberReport;

public interface ManagementReportPort {

    ManagementMemberReport read(UUID reportedId);

    Map<UUID, Integer> readCounts(List<UUID> reportedIds);
}
