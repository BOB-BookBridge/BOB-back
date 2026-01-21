package com.bob.core.inquiry.application;

import static com.bob.global.exception.response.ApplicationError.INQUIRY_ACCESS_DENIED;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bob.core.inquiry.application.dto.query.ReadInquiryDetailQuery;
import com.bob.core.inquiry.application.dto.result.InquiryDetail;
import com.bob.core.inquiry.application.dto.result.InquirySummaries;
import com.bob.core.inquiry.application.port.in.InquiryReader;
import com.bob.core.inquiry.application.port.in.InquirySearcher;
import com.bob.core.inquiry.application.port.out.InquiryMemberPort;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;
import com.bob.global.exception.exceptions.ApplicationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryQueryService implements InquiryReader, InquirySearcher {

    private final InquiryRepository inquiryRepository;

    private final InquiryMemberPort memberPort;

    @Override
    public Inquiry read(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. id: " + inquiryId));
    }

    @Override
    public InquiryDetail readDetail(Long inquiryId, ReadInquiryDetailQuery query) {
        Inquiry inquiry = read(inquiryId);

        verifyAuthorize(query.memberId(), inquiry.getEmail());

        String managerNickname = inquiry.getManagerId() != null
            ? memberPort.read(inquiry.getManagerId()).nickname()
            : null;

        return InquiryDetail.of(inquiry, managerNickname);
    }

    private void verifyAuthorize(UUID memberId, String email) {
        if (!memberPort.isAuthorized(memberId, email))
            throw new ApplicationException(INQUIRY_ACCESS_DENIED);
    }

    @Override
    public InquirySummaries searchByQuery(SearchInquiriesQuery query, Pageable pageable) {
        List<Inquiry> inquiries = inquiryRepository.findInquiries(query, pageable);
        Long totalCount = inquiryRepository.countInquiries(query);

        return new InquirySummaries(totalCount, inquiries);
    }
}
