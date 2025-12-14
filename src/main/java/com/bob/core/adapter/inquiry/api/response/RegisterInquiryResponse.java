package com.bob.core.adapter.inquiry.api.response;

import com.bob.core.domain.inquiry.Inquiry;

public record RegisterInquiryResponse(Long id) {

    public static RegisterInquiryResponse of(Inquiry inquiry) {
        return new RegisterInquiryResponse(inquiry.getId());
    }
}
