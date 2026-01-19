package com.bob.core.inquiry.application;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.inquiry.application.dto.result.InquirySummaries;
import com.bob.core.inquiry.application.port.in.InquiryReader;
import com.bob.core.inquiry.application.port.in.InquirySearcher;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryQueryService implements InquiryReader, InquirySearcher {

    private final InquiryRepository inquiryRepository;

    @Override
    public Inquiry read(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. id: " + inquiryId));
    }

    @Override
    public InquirySummaries searchByQuery(SearchInquiriesQuery query, Pageable pageable) {
        List<Inquiry> inquiries = inquiryRepository.findInquiries(query, pageable);
        Long totalCount = inquiryRepository.countInquiries(query);

        return new InquirySummaries(totalCount, inquiries);
    }
}
