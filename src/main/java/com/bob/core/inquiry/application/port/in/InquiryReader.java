package com.bob.core.inquiry.application.port.in;

import com.bob.core.inquiry.domain.Inquiry;

public interface InquiryReader {

    Inquiry read(Long inquiryId);
}
