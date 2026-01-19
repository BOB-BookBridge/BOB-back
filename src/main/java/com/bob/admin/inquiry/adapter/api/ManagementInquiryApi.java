package com.bob.admin.inquiry.adapter.api;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bob.admin.inquiry.adapter.api.request.ReadManagementInquiriesRequest;
import com.bob.admin.inquiry.application.dto.query.ReadManagementInquiriesQuery;
import com.bob.admin.inquiry.application.port.in.ManagementInquiryReader;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;

@RestController
@RequestMapping("/management/inquiries")
@RequiredArgsConstructor
public class ManagementInquiryApi {

    private final ManagementInquiryReader inquiryReader;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ManagementInquirySummaries readInquiries(
        @Valid ReadManagementInquiriesRequest request,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        ReadManagementInquiriesQuery query = new ReadManagementInquiriesQuery(request.email(), request.status());

        return inquiryReader.readAll(query, pageable);
    }
}
