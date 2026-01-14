package com.bob.core.report.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.core.report.application.dto.result.ReportSummaries;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 검색 테스트")
@ContainerTest
record ReportSearcherTest(ReportSearcher reportSearcher, ReportRepository reportRepository) {

    @BeforeEach
    void setUp() {
        // 대기 1, 닫힘 1, 중복 1, 완료 2
        // MEMBER_ID -> OTHER_MEMBER_ID 신고
        reportRepository.save(ReportFixture.createReport());
        reportRepository.save(ReportFixture.createClosedReport());
        reportRepository.save(ReportFixture.createDuplicatedReport());
        reportRepository.save(ReportFixture.createProcessedReport());
        reportRepository.save(ReportFixture.createProcessedReport());
    }

    @Test
    void 신고_전체_검색() {
        SearchReportsQuery query = new SearchReportsQuery(null, null, null, null);
        var pageable = PageRequest.of(0, 20);

        ReportSummaries summaries = reportSearcher.searchByQuery(query, pageable);

        assertThat(summaries.totalCount()).isEqualTo(5);
    }

    @Test
    void 신고_받은_회원_ID_기반_검색() {
        reportRepository.save(ReportFixture.createReport(OTHER_MEMBER_ID, MEMBER_ID)); // OTHER_MEMBER_ID -> MEMBER_ID 신고

        SearchReportsQuery query = new SearchReportsQuery(null, MEMBER_ID, null, null);
        var pageable = PageRequest.of(0, 20);

        ReportSummaries summaries = reportSearcher.searchByQuery(query, pageable);

        assertThat(summaries.totalCount()).isEqualTo(1);
    }
}
