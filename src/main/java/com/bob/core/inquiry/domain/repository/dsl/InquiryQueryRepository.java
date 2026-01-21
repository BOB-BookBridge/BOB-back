package com.bob.core.inquiry.domain.repository.dsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;

public interface InquiryQueryRepository {

    List<Inquiry> findInquiries(SearchInquiriesQuery query, Pageable pageable);

    Long countInquiries(SearchInquiriesQuery query);
}
