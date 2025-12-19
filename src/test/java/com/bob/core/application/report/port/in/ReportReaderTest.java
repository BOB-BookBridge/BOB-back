package com.bob.core.application.report.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.report.dto.query.ReadReportCountQuery;
import com.bob.core.domain.report.repository.ReportRepository;
import com.bob.core.domain.report.repository.projection.ReportCount;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 조회 테스트")
@ContainerTest
record ReportReaderTest(ReportReader reportReader, ReportRepository reportRepository) {

    @Test
    void 신고_횟수_조회() {
        reportRepository.save(ReportFixture.createReport());
        List<UUID> reportedIds = List.of(MEMBER_ID, OTHER_MEMBER_ID);

        var query = new ReadReportCountQuery(reportedIds);

        List<ReportCount> result = reportReader.readReportedCounts(query);

        assertThat(result).hasSize(2)
            .satisfiesExactlyInAnyOrder(
                reportCount -> {
                    assertThat(reportCount.getReportedId()).isEqualTo(MEMBER_ID);
                    assertThat(reportCount.getCount()).isEqualTo(0);
                },
                reportCount -> {
                    assertThat(reportCount.getReportedId()).isEqualTo(OTHER_MEMBER_ID);
                    assertThat(reportCount.getCount()).isEqualTo(1);
                }
            );
    }
}
