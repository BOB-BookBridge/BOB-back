package com.bob.core.inquiry.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bob.core.inquiry.adapter.api.request.RegisterInquiryRequest;
import com.bob.core.inquiry.adapter.api.response.RegisterInquiryResponse;
import com.bob.core.inquiry.domain.Inquiry;
import com.bob.core.inquiry.domain.InquiryStatus;
import com.bob.core.inquiry.domain.repository.InquiryRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.fixture.inquiry.domain.InquiryFixture;
import com.bob.support.util.AssertThatUtils;

@DisplayName("문의 API 테스트")
@BobApiTest
record InquiryApiTest(MockMvcTester mvcTester, InquiryRepository inquiryRepository, ObjectMapper objectMapper) {

    @Test
    void 문의() throws JsonProcessingException, UnsupportedEncodingException {
        RegisterInquiryRequest request = new RegisterInquiryRequest("test@test.com", "제목", "내용");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/inquiries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.notNull());

        RegisterInquiryResponse response =
            objectMapper.readValue(result.getResponse().getContentAsString(), RegisterInquiryResponse.class);

        Inquiry inquiry = inquiryRepository.findById(response.id()).orElseThrow();
        assertThat(inquiry.getEmail()).isEqualTo("test@test.com");
        assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.PENDING);
    }

    @Test
    void 문의_상세_조회() {
        Inquiry inquiry = inquiryRepository.save(InquiryFixture.createProcessedInquiry());

        setAuthentication(MANAGER_ID);

        MvcTestResult result = mvcTester.get()
            .uri("/inquiries/{inquiryId}", inquiry.getId())
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.notNull())
            .hasPathSatisfying("$.status", AssertThatUtils.equalsTo("PROCESSED"));
    }

    void setAuthentication(UUID memberId) {
        MemberDetails principal = new MemberDetails(memberId, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
