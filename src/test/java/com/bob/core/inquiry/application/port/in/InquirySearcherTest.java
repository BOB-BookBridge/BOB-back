package com.bob.core.inquiry.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_EMAIL;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.inquiry.application.dto.result.InquirySummaries;
import com.bob.core.inquiry.domain.InquiryStatus;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.core.inquiry.domain.repository.dsl.query.SearchInquiriesQuery;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("문의 검색 테스트")
@ContainerTest
record InquirySearcherTest(InquirySearcher inquirySearcher, InquiryRepository inquiryRepository) {

    @BeforeEach
    void setUp() {
        // MEMBER_ID 문의 : 대기 1, 닫힘 1, 완료 2
        inquiryRepository.save(InquiryFixture.createInquiry());
        inquiryRepository.save(InquiryFixture.createClosedInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        // OTHER_MEMBER_ID 문의 : 대기 1
        inquiryRepository.save(InquiryFixture.createInquiry(OTHER_MEMBER_EMAIL));
    }

    @Test
    void 문의_전체_검색() {
        var query = new SearchInquiriesQuery(null, null);
        var pageable = PageRequest.of(0, 20);

        InquirySummaries result = inquirySearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(5);
    }

    @Test
    void 문의_회원_이메일_기반_검색() {
        var query = new SearchInquiriesQuery(MEMBER_EMAIL, null);
        var pageable = PageRequest.of(0, 20);

        InquirySummaries result = inquirySearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(4);
        assertThat(result.inquiries())
            .extracting("email")
            .containsOnly(MEMBER_EMAIL);
    }

    @Test
    void 문의_상태_기반_검색() {
        var query = new SearchInquiriesQuery(null, InquiryStatus.PROCESSED);
        var pageable = PageRequest.of(0, 20);

        InquirySummaries result = inquirySearcher.searchByQuery(query, pageable);

        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.inquiries())
            .extracting("status")
            .containsOnly(InquiryStatus.PROCESSED);
    }
}
