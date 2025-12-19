package com.bob.core.application.management.port.in;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.domain.report.Report;
import com.bob.core.domain.report.repository.ReportRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("관리 회원 조회 테스트")
@ContainerTest
record ManagementMemberReaderTest(ManagementMemberReader memberReader, ReportRepository reportRepository) {

    @Test
    void 관리_회원_목록_조회() {
        var pageable = PageRequest.of(0, 20);

        var result = memberReader.readAll(null, null, pageable);

        assertThat(result.totalCount()).isNotZero();
        assertThat(result.members()).isNotEmpty();

        // 신고 횟수 검증
        Report report = ReportFixture.createReport();
        reportRepository.save(report);

        var containsReportCountResult = memberReader.readAll("NICKNAME", "other", pageable);
        assertThat(containsReportCountResult.members().get(0).getReportCount()).isEqualTo(1);
    }
}
