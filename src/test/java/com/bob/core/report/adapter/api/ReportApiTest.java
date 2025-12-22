package com.bob.core.report.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MOCK_MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.BeforeEach;
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

import com.bob.core.report.adapter.api.request.RegisterReportRequest;
import com.bob.core.report.adapter.api.response.RegisterReportResponse;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.util.AssertThatUtils;

@DisplayName("신고 API 테스트")
@BobApiTest
record ReportApiTest(MockMvcTester mvcTester, ReportRepository reportRepository, ObjectMapper objectMapper) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 게시글_신고() throws JsonProcessingException, UnsupportedEncodingException {
        RegisterReportRequest request = new RegisterReportRequest(MOCK_MEMBER_ID, "부적절한 콘텐츠");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/reports/posts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.notNull());

        RegisterReportResponse response =
            objectMapper.readValue(result.getResponse().getContentAsString(), RegisterReportResponse.class);

        Report report = reportRepository.findById(response.id()).orElseThrow();
        assertThat(report.getReason()).isEqualTo(request.reason());
        assertThat(report.getTarget()).isEqualTo(ReportTarget.POST);
        assertThat(report.getReporterId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(report.getReportedId()).isEqualTo(MOCK_MEMBER_ID);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
    }

    @Test
    void 채팅_신고() throws JsonProcessingException, UnsupportedEncodingException {
        RegisterReportRequest request = new RegisterReportRequest(MOCK_MEMBER_ID, "욕설/비방");
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/reports/chats/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.notNull());

        RegisterReportResponse response =
            objectMapper.readValue(result.getResponse().getContentAsString(), RegisterReportResponse.class);

        Report report = reportRepository.findById(response.id()).orElseThrow();
        assertThat(report.getReason()).isEqualTo(request.reason());
        assertThat(report.getTarget()).isEqualTo(ReportTarget.CHAT);
        assertThat(report.getReporterId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(report.getReportedId()).isEqualTo(MOCK_MEMBER_ID);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(OTHER_MEMBER_ID, true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
