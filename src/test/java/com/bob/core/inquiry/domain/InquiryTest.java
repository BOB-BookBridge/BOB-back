package com.bob.core.inquiry.domain;

import static com.bob.core.inquiry.domain.InquiryStatus.CLOSED;
import static com.bob.core.inquiry.domain.InquiryStatus.PENDING;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("문의 도메인 테스트")
class InquiryTest {

    @Test
    void 문의_생성() {
        Inquiry inquiry = Inquiry.createInquiry("test@email.com", "제목", "내용");

        assertThat(inquiry.getStatus()).isEqualTo(PENDING);
        assertThat(inquiry.getTitle()).isEqualTo("제목");
        assertThat(inquiry.getContent()).isEqualTo("내용");
        assertThat(inquiry.getCreatedAt()).isNotNull();
        assertThat(inquiry.getManagerId()).isNull();
        assertThat(inquiry.getProcessedAt()).isNull();
    }

    @Test
    void 문의_검토() {
        Inquiry inquiry = InquiryFixture.createInquiry();

        inquiry.review(MEMBER_ID);

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.IN_REVIEW);
    }

    @Test
    void 문의_검토_시_대기상태가_아니면_예외가_발생한다() {
        Inquiry inquiry = InquiryFixture.createInquiry();
        inquiry.review(MEMBER_ID);

        assertThatThrownBy(() -> inquiry.review(MEMBER_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("처리 대기 상태가 아닙니다");
    }

    @Test
    void 문의_완료_처리() {
        Inquiry inquiry = InquiryFixture.createInquiry();
        inquiry.review(MEMBER_ID);

        inquiry.process("답변");

        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.PROCESSED);
    }

    @Test
    void 문의_완료_처리_시_검토_상태가_아니면_예외가_발생한다() {
        Inquiry inquiry = InquiryFixture.createInquiry();
        assertThat(inquiry.getStatus()).isEqualTo(PENDING);

        assertThatThrownBy(() -> inquiry.process("답변"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("검토 상태가 아닙니다");
    }

    @Test
    void 문의_취소_처리() {
        Inquiry inquiry = InquiryFixture.createInquiry();
        inquiry.review(MEMBER_ID);

        inquiry.close();

        assertThat(inquiry.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void 문의_취소_처리_시_검토_상태가_아니면_예외가_발생한다() {
        Inquiry inquiry = InquiryFixture.createInquiry();
        assertThat(inquiry.getStatus()).isEqualTo(PENDING);

        assertThatThrownBy(() -> inquiry.process("답변"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("검토 상태가 아닙니다");
    }
}
