package com.bob.admin.report.adapter.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
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

import com.bob.admin.report.adapter.api.request.ProcessManagementReportStatusRequest;
import com.bob.admin.report.application.port.result.ManagementReportDetail;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.security.model.MemberDetails;
import com.bob.support.annotation.BobApiTest;
import com.bob.support.fixture.report.domain.ReportFixture;
import com.bob.support.util.AssertThatUtils;

@DisplayName("관리자 - 신고 관리 API 테스트")
@BobApiTest
record ManagementReportApiTest(MockMvcTester mvcTester, ObjectMapper objectMapper, ReportRepository reportRepository) {

    @BeforeEach
    void setUp() {
        setAuthentication();
    }

    @Test
    void 신고_목록_조회() throws JsonProcessingException, UnsupportedEncodingException {
        reportRepository.save(ReportFixture.createReport());
        reportRepository.save(ReportFixture.createProcessedReport());

        MvcTestResult result = mvcTester.get().uri("/management/reports")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.reports", AssertThatUtils.notNull())
            .hasPathSatisfying("$.totalCount", AssertThatUtils.notNull());

        ManagementReportSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementReportSummaries.class);

        assertThat(response.totalCount()).isNotZero();
        assertThat(response.reports()).isNotEmpty();
        assertThat(response.reports().get(0).reporter()).isNotNull();
        assertThat(response.reports().get(0).reported()).isNotNull();
    }

    @Test
    void 신고_목록_조회_신고자_이메일_필터링() throws JsonProcessingException, UnsupportedEncodingException {
        reportRepository.save(ReportFixture.createReport());

        MvcTestResult result = mvcTester.get()
            .uri("/management/reports?reporterEmail=test@test.com")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.reports", AssertThatUtils.notNull());

        ManagementReportSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementReportSummaries.class);

        assertThat(response.reports()).isNotEmpty();
    }

    @Test
    void 신고_목록_조회_타입_필터링() throws JsonProcessingException, UnsupportedEncodingException {
        reportRepository.save(ReportFixture.createReport());

        MvcTestResult result = mvcTester.get()
            .uri("/management/reports?type=POST")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.reports", AssertThatUtils.notNull());

        ManagementReportSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementReportSummaries.class);

        assertThat(response.reports()).isNotEmpty();
        assertThat(response.reports())
            .allSatisfy(report -> assertThat(report.type()).isEqualTo("POST"));
    }

    @Test
    void 신고_목록_조회_상태_필터링() throws JsonProcessingException, UnsupportedEncodingException {
        reportRepository.save(ReportFixture.createProcessedReport());

        MvcTestResult result = mvcTester.get()
            .uri("/management/reports?status=PROCESSED")
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.reports", AssertThatUtils.notNull());

        ManagementReportSummaries response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementReportSummaries.class);

        assertThat(response.reports()).isNotEmpty();
        assertThat(response.reports())
            .allSatisfy(report -> assertThat(report.status()).isEqualTo("PROCESSED"));
    }

    @Test
    void 신고_상세_조회() throws JsonProcessingException, UnsupportedEncodingException {
        Report report = reportRepository.save(ReportFixture.createProcessedReport());

        MvcTestResult result = mvcTester.get()
            .uri("/management/reports/{reportId}", report.getId())
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.id", AssertThatUtils.equalsTo(report.getId().intValue()))
            .hasPathSatisfying("$.status", AssertThatUtils.notNull())
            .hasPathSatisfying("$.type", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reporter", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reporter.id", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reporter.email", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reporter.nickname", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reported", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reported.id", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reported.email", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reported.nickname", AssertThatUtils.notNull())
            .hasPathSatisfying("$.reportedContent", AssertThatUtils.notNull())
            .hasPathSatisfying("$.managerNickname", AssertThatUtils.notNull());

        ManagementReportDetail response =
            objectMapper.readValue(result.getResponse().getContentAsString(), ManagementReportDetail.class);

        assertThat(response.id()).isEqualTo(report.getId());
        assertThat(response.reporter()).isNotNull();
        assertThat(response.reporter().id()).isNotNull();
        assertThat(response.reporter().email()).isNotBlank();
        assertThat(response.reporter().nickname()).isNotBlank();
        assertThat(response.reported()).isNotNull();
        assertThat(response.reported().id()).isNotNull();
        assertThat(response.reported().email()).isNotBlank();
        assertThat(response.reported().nickname()).isNotBlank();
        assertThat(response.reportedContent()).isNotNull();
        assertThat(response.managerNickname()).isNotBlank();
    }

    @Test
    void 신고_처리() throws JsonProcessingException {
        Report report = reportRepository.save(ReportFixture.createInReviewReport());

        var request = new ProcessManagementReportStatusRequest("PROCESSED", null);
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.patch().uri("/management/reports/{reportId}", report.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .exchange();

        assertThat(result)
            .hasStatus2xxSuccessful()
            .bodyJson()
            .hasPathSatisfying("$.result", AssertThatUtils.equalsTo("UPDATED"));
    }

    void setAuthentication() {
        MemberDetails principal = new MemberDetails(MANAGER_ID, "ADMIN", true);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
