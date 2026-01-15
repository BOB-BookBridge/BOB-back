package com.bob.core.report.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.report.application.dto.query.ReadReportCountQuery;
import com.bob.core.report.application.dto.query.ReadReportQuery;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.domain.repository.projection.ReportCount;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 조회 테스트")
@ContainerTest
record ReportReaderTest(ReportReader reportReader, ReportRepository reportRepository) {

    @Test
    void 신고_내역_조회() {
        reportRepository.save(ReportFixture.createReport());
        ReadReportQuery query = new ReadReportQuery(OTHER_MEMBER_ID);

        List<Report> reports = reportReader.read(query);

        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getReportedId()).isEqualTo(OTHER_MEMBER_ID);
    }

    @Test
    void 신고_처리_횟수_조회() {
        reportRepository.save(ReportFixture.createReport()); // 제외
        reportRepository.save(ReportFixture.createProcessedReport()); // 포함
        List<UUID> reportedIds = List.of(MEMBER_ID, OTHER_MEMBER_ID);

        var query = new ReadReportCountQuery(reportedIds);

        List<ReportCount> result = reportReader.readProcessedReportCounts(query);

        assertThat(result).hasSize(1).satisfiesExactlyInAnyOrder(
            reportCount -> {
                assertThat(reportCount.getReportedId()).isEqualTo(OTHER_MEMBER_ID);
                assertThat(reportCount.getCount()).isEqualTo(1);
            }
        );
    }
}
