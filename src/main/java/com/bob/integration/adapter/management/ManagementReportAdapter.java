package com.bob.integration.adapter.management;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.bob.core.application.management.port.out.ManagementReportPort;
import com.bob.core.application.report.dto.query.ReadReportCountQuery;
import com.bob.core.application.report.port.in.ReportReader;
import com.bob.core.domain.report.repository.projection.ReportCount;

@Component
@RequiredArgsConstructor
public class ManagementReportAdapter implements ManagementReportPort {

    private final ReportReader reportReader;

    @Override
    public Map<UUID, Integer> readCounts(List<UUID> reportedIds) {
        ReadReportCountQuery query = new ReadReportCountQuery(reportedIds);

        return reportReader.readReportedCounts(query).stream()
            .collect(Collectors.toMap(ReportCount::getReportedId, ReportCount::getCount));
    }
}
