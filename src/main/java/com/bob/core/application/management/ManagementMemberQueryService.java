package com.bob.core.application.management;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.management.port.in.ManagementMemberReader;
import com.bob.core.application.management.port.out.ManagementMemberPort;
import com.bob.core.application.management.port.out.ManagementReportPort;
import com.bob.core.application.management.port.result.ManagementMember;
import com.bob.core.application.management.port.result.ManagementMembersResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementMemberQueryService implements ManagementMemberReader {

    private final ManagementMemberPort memberPort;
    private final ManagementReportPort reportPort;

    @Override
    public ManagementMembersResult readMembers(String key, String keyword, Pageable pageable) {
        ManagementMembersResult result = memberPort.search(key, keyword, pageable);

        List<UUID> memberIds = result.members().stream()
            .map(ManagementMember::getId)
            .toList();

        Map<UUID, Integer> reportCounts = reportPort.readCounts(memberIds);
        result.members().forEach(member ->
            member.updateReportCount(reportCounts.getOrDefault(member.getId(), 0))
        );

        return result;
    }
}
