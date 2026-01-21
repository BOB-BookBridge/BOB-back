package com.bob.admin.inquiry.application;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.admin.inquiry.application.dto.query.ReadManagementInquiriesQuery;
import com.bob.admin.inquiry.application.port.in.ManagementInquiryReader;
import com.bob.admin.inquiry.application.port.out.ManagementInquiryPort;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManagementInquiryQueryService implements ManagementInquiryReader {

    private final ManagementInquiryPort inquiryPort;

    @Override
    public ManagementInquirySummaries readAll(ReadManagementInquiriesQuery query, Pageable pageable) {
        return inquiryPort.readAll(query.email(), query.status(), pageable);
    }
}
