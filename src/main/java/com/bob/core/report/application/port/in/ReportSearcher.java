package com.bob.core.report.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.report.application.dto.result.ReportSummaries;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;

public interface ReportSearcher {

    ReportSummaries searchByQuery(SearchReportsQuery query, Pageable pageable);
}
