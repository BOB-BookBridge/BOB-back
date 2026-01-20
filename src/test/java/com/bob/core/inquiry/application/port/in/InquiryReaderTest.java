package com.bob.core.inquiry.application.port.in;

import static com.bob.core.inquiry.domain.InquiryStatus.PROCESSED;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.inquiry.application.dto.query.ReadInquiryDetailQuery;
import com.bob.core.inquiry.application.dto.result.InquiryDetail;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("문의 조회 테스트")
@ContainerTest
record InquiryReaderTest(InquiryReader inquiryReader, InquiryRepository inquiryRepository, EntityManager em) {

    @Test
    void 문의_조회() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createInquiry());

        em.flush();
        em.clear();

        Inquiry result = inquiryReader.read(inquiry.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(inquiry.getId());
    }

    @Test
    void 문의_조회_시_존재하지_않으면_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> inquiryReader.read(nonExistentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("문의를 찾을 수 없습니다.");
    }

    @Test
    void 문의_상세_조회() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        em.flush();
        em.clear();

        ReadInquiryDetailQuery query = new ReadInquiryDetailQuery(MANAGER_ID);

        InquiryDetail result = inquiryReader.readDetail(inquiry.getId(), query);

        assertThat(result).isNotNull();
        assertThat(result.managerNickname()).isNotNull();
        assertThat(result.status()).isEqualTo(PROCESSED.name());
    }

    @Test
    void 문의_상세_조회_시_소유자_외_일반_사용자가_접근하면_사용자_예외가_발생한다() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        em.flush();
        em.clear();

        ReadInquiryDetailQuery query = new ReadInquiryDetailQuery(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> inquiryReader.readDetail(inquiry.getId(), query))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.INQUIRY_ACCESS_DENIED.getMessage());
    }
}
