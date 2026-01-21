package com.bob.core.inquiry.application.dto.result;

import java.util.List;

import com.bob.core.inquiry.domain.Inquiry;

public record InquirySummaries(Long totalCount, List<Inquiry> inquiries) {

}
