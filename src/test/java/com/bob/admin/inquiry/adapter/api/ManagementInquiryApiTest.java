package com.bob.admin.inquiry.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.admin.inquiry.application.port.result.ManagementInquirySummaries;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;
import com.bob.support.util.AssertThatUtils;

@DisplayName("관리자 - 문의 관리 API 테스트")
@BobApiTest
record ManagementInquiryApiTest(MockMvcTester tester, ObjectMapper objectMapper, InquiryRepository inquiryRepository) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 문의_목록_조회() throws JsonProcessingException, UnsupportedEncodingException {
        inquiryRepository.save(InquiryFixture.createInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        MvcTestResult result = tester.get().uri("/management/inquiries")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.inquiries", AssertThatUtils.notNull())
            .hasPathSatisfying("$.totalCount", AssertThatUtils.notNull());

        ManagementInquirySummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementInquirySummaries.class);

        assertThat(response.totalCount()).isNotZero();
        assertThat(response.inquiries()).isNotEmpty();
    }

    @Test
    void 문의_목록_조회_이메일_검색() throws JsonProcessingException, UnsupportedEncodingException {
        inquiryRepository.save(InquiryFixture.createInquiry("search@email.com"));

        MvcTestResult result = tester.get()
            .uri("/management/inquiries?email=search")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.inquiries", AssertThatUtils.notNull());

        ManagementInquirySummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementInquirySummaries.class);

        assertThat(response.inquiries()).isNotEmpty();
        assertThat(response.inquiries())
            .allSatisfy(inquiry -> assertThat(inquiry.email()).containsIgnoringCase("search"));
    }

    @Test
    void 문의_목록_조회_상태_필터링() throws JsonProcessingException, UnsupportedEncodingException {
        inquiryRepository.save(InquiryFixture.createInquiry());
        inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        MvcTestResult result = tester.get()
            .uri("/management/inquiries?status=PROCESSED")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.inquiries", AssertThatUtils.notNull());

        ManagementInquirySummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementInquirySummaries.class);

        assertThat(response.inquiries()).isNotEmpty();
        assertThat(response.inquiries())
            .allSatisfy(inquiry -> assertThat(inquiry.status()).isEqualTo("PROCESSED"));
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
