package com.bob.core.inquiry.adapter.out;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.inquiry.application.port.out.ManagementInquiryPort;
import com.bob.admin.inquiry.application.port.result.ManagementInquiry;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;
import com.bob.core.inquiry.application.dto.result.InquirySummaries;
import com.bob.core.inquiry.application.port.in.InquirySearcher;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;
import com.bob.core.member.application.port.in.MemberReader;

@Component
@RequiredArgsConstructor
public class ManagementInquiryAdapter implements ManagementInquiryPort {

    private final InquirySearcher inquirySearcher;

    private final MemberReader memberReader;

    @Override
    public ManagementInquirySummaries readAll(String email, String status, Pageable pageable) {
        SearchInquiriesQuery query = SearchInquiriesQuery.of(email, status);

        InquirySummaries summaries = inquirySearcher.searchByQuery(query, pageable);

        List<ManagementInquiry> managementInquiries = summaries.inquiries().stream()
            .map(this::convert)
            .toList();

        return new ManagementInquirySummaries(summaries.totalCount(), managementInquiries);
    }

    private ManagementInquiry convert(Inquiry inquiry) {
        String managerNickname = inquiry.getManagerId() != null
            ? memberReader.read(inquiry.getManagerId()).getNickname()
            : null;

        return ManagementInquiry.builder()
            .id(inquiry.getId())
            .status(inquiry.getStatus().name())
            .title(inquiry.getTitle())
            .email(inquiry.getEmail())
            .managerNickname(managerNickname)
            .createdAt(inquiry.getCreatedAt())
            .processedAt(inquiry.getProcessedAt())
            .build();
    }
}
