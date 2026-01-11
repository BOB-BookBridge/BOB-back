package com.bob.core.report.domain.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;

public interface ReportQueryRepository {

    List<Report> findReports(SearchReportsQuery query, Pageable pageable);

    Long countReports(SearchReportsQuery query);
}
