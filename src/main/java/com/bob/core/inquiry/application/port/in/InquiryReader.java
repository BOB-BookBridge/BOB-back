package com.bob.core.inquiry.application.port.in;

import com.bob.core.inquiry.application.dto.query.ReadInquiryDetailQuery;
import com.bob.core.inquiry.application.dto.result.InquiryDetail;
import com.bob.core.inquiry.domain.Inquiry;

public interface InquiryReader {

    Inquiry read(Long inquiryId);

    InquiryDetail readDetail(Long inquiryId, ReadInquiryDetailQuery query);
}
