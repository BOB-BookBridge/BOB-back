package com.bob.core.management.application;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.management.application.dto.result.ManagementMemberDetail;
import com.bob.core.management.application.port.in.ManagementMemberReader;
import com.bob.core.management.application.port.out.ManagementMemberPort;
import com.bob.core.management.application.port.out.ManagementPostPort;
import com.bob.core.management.application.port.out.ManagementReportPort;
import com.bob.core.management.application.port.out.ManagementTradePort;
import com.bob.core.management.application.port.result.ManagementMember;
import com.bob.core.management.application.port.result.ManagementMemberActivity;
import com.bob.core.management.application.port.result.ManagementMemberReport;
import com.bob.core.management.application.port.result.ManagementMemberSummaries;
import com.bob.core.management.application.port.result.activity.ManagementMemberPost;
import com.bob.core.management.application.port.result.activity.ManagementMemberTrade;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementMemberQueryService implements ManagementMemberReader {

    private final ManagementMemberPort memberPort;
    private final ManagementReportPort reportPort;
    private final ManagementPostPort postPort;
    private final ManagementTradePort tradePort;

    @Override
    public ManagementMemberSummaries readAll(String key, String keyword, Pageable pageable) {
        ManagementMemberSummaries summaries = memberPort.search(key, keyword, pageable);

        List<UUID> memberIds = summaries.members().stream().map(ManagementMember::getId).toList();

        Map<UUID, Integer> reportCounts = reportPort.readCounts(memberIds);
        summaries.members().forEach(member -> member.updateReportCount(reportCounts.getOrDefault(member.getId(), 0)));

        return summaries;
    }

    @Override
    public ManagementMemberDetail readDetail(UUID memberId) {
        ManagementMember member = memberPort.read(memberId);

        ManagementMemberPost posts = postPort.read(memberId);
        ManagementMemberTrade trades = tradePort.read(memberId);
        ManagementMemberActivity activity = new ManagementMemberActivity(posts, trades);

        ManagementMemberReport reports = reportPort.read(memberId);

        member.updateReportCount(reports.chat().count() + reports.post().count());

        return new ManagementMemberDetail(member, activity, reports);
    }
}
