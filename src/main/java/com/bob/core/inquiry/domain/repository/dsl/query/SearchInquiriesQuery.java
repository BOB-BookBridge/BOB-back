package com.bob.core.inquiry.domain.repository.dsl.query;

import com.bob.core.inquiry.domain.InquiryStatus;

public record SearchInquiriesQuery(String email, InquiryStatus status) {

    public static SearchInquiriesQuery of(String email, String status) {
        return new SearchInquiriesQuery(
            email,
            status != null ? InquiryStatus.valueOf(status.toUpperCase()) : null
        );
    }
}
