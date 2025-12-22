package com.bob.admin.member.application.port.out;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.bob.admin.member.application.port.result.ManagementMemberReport;

public interface ManagementMemberReportPort {

    ManagementMemberReport read(UUID reportedId);

    Map<UUID, Integer> readCounts(List<UUID> reportedIds);
}
