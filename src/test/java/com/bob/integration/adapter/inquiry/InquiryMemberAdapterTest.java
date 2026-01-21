package com.bob.integration.adapter.inquiry;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("문의 회원 검증 테스트")
@ContainerTest
record InquiryMemberAdapterTest(InquiryMemberAdapter memberAdapter, InquiryRepository inquiryRepository) {

    @Test
    void 관리자_접근_허용() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry());

        boolean result = memberAdapter.isAuthorized(MANAGER_ID, inquiry.getEmail());

        assertThat(result).isTrue();
    }

    @Test
    void 소유자_접근_허용() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry());

        boolean result = memberAdapter.isAuthorized(MEMBER_ID, inquiry.getEmail());

        assertThat(result).isTrue();
    }

    @Test
    void 소유자_외_일반_회원_접근_거부() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry());

        boolean result = memberAdapter.isAuthorized(OTHER_MEMBER_ID, inquiry.getEmail());

        assertThat(result).isFalse();
    }
}
