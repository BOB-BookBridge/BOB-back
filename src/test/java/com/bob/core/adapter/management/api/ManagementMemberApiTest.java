package com.bob.core.adapter.management.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
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

import com.bob.core.application.management.port.result.ManagementMemberSummaries;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("관리자 - 회원 관리 API 테스트")
@BobApiTest
record ManagementMemberApiTest(MockMvcTester mvcTester, ObjectMapper objectMapper) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 관리_회원_목록_조회() throws JsonProcessingException, UnsupportedEncodingException {
        MvcTestResult result = mvcTester.get().uri("/management/members")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.members", AssertThatUtils.notNull())
            .hasPathSatisfying("$.totalCount", AssertThatUtils.notNull());

        ManagementMemberSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementMemberSummaries.class);

        assertThat(response.totalCount()).isNotZero();
        assertThat(response.members()).isNotNull();
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MEMBER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
