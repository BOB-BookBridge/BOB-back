package com.bob.core.report.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.report.application.dto.query.ReadReportCountQuery;
import com.bob.core.report.application.dto.query.ReadReportQuery;
import com.bob.core.report.application.dto.result.ReportSummaries;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.application.port.in.ReportSearcher;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;
import com.bob.core.report.domain.repository.projection.ReportCount;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportQueryService implements ReportReader, ReportSearcher {

    private final ReportRepository reportRepository;

    @Override
    public List<Report> read(ReadReportQuery query) {
        return reportRepository.findAllByReportedId(query.reportedId());
    }

    @Override
    public List<ReportCount> readProcessedReportCounts(ReadReportCountQuery query) {
        return reportRepository.countProcessedByReportedIds(query.reportedIds());
    }

    @Override
    public ReportSummaries searchByQuery(SearchReportsQuery query, Pageable pageable) {
        List<Report> reports = reportRepository.findReports(query, pageable);
        Long totalCount = reportRepository.countReports(query);

        return new ReportSummaries(totalCount, reports);
    }
}
