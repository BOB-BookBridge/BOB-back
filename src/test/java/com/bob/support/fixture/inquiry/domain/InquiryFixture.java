package com.bob.support.fixture.inquiry.domain;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;

import com.bob.core.inquiry.domain.Inquiry;

public class InquiryFixture {

    public static Inquiry createInquiry() {
        return Inquiry.createInquiry("test@test.com", "제목", "내용");
    }

    public static Inquiry createInquiry(String email) {
        return Inquiry.createInquiry(email, "제목", "내용");
    }

    public static Inquiry createInReviewInquiry() {
        Inquiry inquiry = createInquiry();
        inquiry.review(MANAGER_ID);
        return inquiry;
    }

    public static Inquiry createProcessedInquiry() {
        Inquiry inquiry = createInReviewInquiry();
        inquiry.process("답변 내용입니다");
        return inquiry;
    }

    public static Inquiry createClosedInquiry() {
        Inquiry inquiry = createInReviewInquiry();
        inquiry.close();
        return inquiry;
    }
}
