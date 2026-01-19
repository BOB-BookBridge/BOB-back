package com.bob.admin.inquiry.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.admin.inquiry.application.dto.query.ReadManagementInquiriesQuery;
import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;

@DisplayName("관리자 - 문의 조회 테스트")
@ContainerTest
record ManagementInquiryReaderTest(ManagementInquiryReader inquiryReader, InquiryRepository inquiryRepository) {

    @Test
    void 문의_목록_조회() {
        inquiryRepository.save(InquiryFixture.createInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());
        inquiryRepository.save(InquiryFixture.createClosedInquiry());

        var query = new ReadManagementInquiriesQuery(null, null);
        var pageable = PageRequest.of(0, 20);

        ManagementInquirySummaries result = inquiryReader.readAll(query, pageable);

        assertThat(result.totalCount()).isGreaterThanOrEqualTo(3);
        assertThat(result.inquiries()).isNotEmpty();
        assertThat(result.inquiries())
            .allSatisfy(inquiry -> {
                assertThat(inquiry.id()).isNotNull();
                assertThat(inquiry.status()).isNotNull();
                assertThat(inquiry.title()).isNotNull();
                assertThat(inquiry.email()).isNotNull();
            });
    }

    @Test
    void 문의_목록_조회_이메일_검색() {
        inquiryRepository.save(InquiryFixture.createInquiry("searchtest@email.com"));

        var query = new ReadManagementInquiriesQuery("searchtest", null);
        var pageable = PageRequest.of(0, 20);

        ManagementInquirySummaries result = inquiryReader.readAll(query, pageable);

        assertThat(result.inquiries()).isNotEmpty();
        assertThat(result.inquiries())
            .allSatisfy(inquiry -> assertThat(inquiry.email()).containsIgnoringCase("searchtest"));
    }

    @Test
    void 문의_목록_조회_상태_필터링() {
        inquiryRepository.save(InquiryFixture.createInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        var query = new ReadManagementInquiriesQuery(null, "PROCESSED");
        var pageable = PageRequest.of(0, 20);

        ManagementInquirySummaries result = inquiryReader.readAll(query, pageable);

        assertThat(result.inquiries()).isNotEmpty();
        assertThat(result.inquiries())
            .allSatisfy(inquiry -> assertThat(inquiry.status()).isEqualTo("PROCESSED"));
    }
}
