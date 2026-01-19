package com.bob.core.inquiry.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.core.inquiry.application.dto.result.InquirySummaries;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;

public interface InquirySearcher {

    InquirySummaries searchByQuery(SearchInquiriesQuery query, Pageable pageable);
}
