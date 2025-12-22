package com.bob.core.inquiry.adapter.api.response;

import com.bob.core.inquiry.domain.Inquiry;

public record RegisterInquiryResponse(Long id) {

    public static RegisterInquiryResponse of(Inquiry inquiry) {
        return new RegisterInquiryResponse(inquiry.getId());
    }
}
