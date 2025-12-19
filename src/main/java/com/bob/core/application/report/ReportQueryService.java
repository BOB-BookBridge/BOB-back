package com.bob.core.application.report;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.application.report.dto.query.ReadReportCountQuery;
import com.bob.core.application.report.port.in.ReportReader;
import com.bob.core.domain.report.repository.ReportRepository;
import com.bob.core.domain.report.repository.projection.ReportCount;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportQueryService implements ReportReader {

    private final ReportRepository reportRepository;

    @Override
    public List<ReportCount> readReportedCounts(ReadReportCountQuery query) {
        return reportRepository.countByReportedIds(query.reportedIds());
    }
}
