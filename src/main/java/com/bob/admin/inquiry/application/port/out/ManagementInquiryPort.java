package com.bob.admin.inquiry.application.port.out;

import org.springframework.data.domain.Pageable;

import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;

public interface ManagementInquiryPort {

    ManagementInquirySummaries readAll(String email, String status, Pageable pageable);
}
