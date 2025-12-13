package com.bob.support.fixture.inquiry.domain;

import com.bob.core.domain.inquiry.Inquiry;

public class InquiryFixture {

    public static Inquiry createInquiry() {
        return Inquiry.createInquiry("test@email.com", "제목", "내용");
    }
}
