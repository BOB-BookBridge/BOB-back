package com.bob.admin.inquiry.application.port.in;

import org.springframework.data.domain.Pageable;

import com.bob.admin.inquiry.application.dto.query.ReadManagementInquiriesQuery;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;

public interface ManagementInquiryReader {

    ManagementInquirySummaries readAll(ReadManagementInquiriesQuery query, Pageable pageable);
}
