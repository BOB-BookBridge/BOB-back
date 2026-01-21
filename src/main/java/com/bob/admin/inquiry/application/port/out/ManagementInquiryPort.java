package com.bob.admin.inquiry.application.port.out;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;
import com.bob.admin.report.application.port.result.ReportedMemberInfo;

public interface ManagementInquiryPort {

    ManagementInquirySummaries readAll(String email, String status, Pageable pageable);

    void changeStatus(Long inquiryId, UUID managerId, String status, String reply);
}
