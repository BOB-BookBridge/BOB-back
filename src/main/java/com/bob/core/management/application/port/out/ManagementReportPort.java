package com.bob.core.management.application.port.out;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bob.core.management.application.port.result.ManagementMemberReport;

public interface ManagementReportPort {

    ManagementMemberReport read(UUID reportedId);

    Map<UUID, Integer> readCounts(List<UUID> reportedIds);
}
