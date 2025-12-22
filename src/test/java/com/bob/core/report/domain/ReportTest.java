package com.bob.core.report.domain;

import static com.bob.core.report.domain.ReportStatus.CLOSED;
import static com.bob.core.report.domain.ReportStatus.DUPLICATED;
import static com.bob.core.report.domain.ReportStatus.PENDING;
import static com.bob.core.report.domain.ReportTarget.POST;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MOCK_MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("신고 도메인 테스트")
class ReportTest {

    @Test
    void 신고_생성() {
        Report report = Report.createReport(POST, 1L, "사기/허위", OTHER_MEMBER_ID, MOCK_MEMBER_ID);

        assertThat(report.getTarget()).isEqualTo(POST);
        assertThat(report.getStatus()).isEqualTo(PENDING);
        assertThat(report.getReason()).isEqualTo("사기/허위");
        assertThat(report.getCreatedAt()).isNotNull();
        assertThat(report.getManagerId()).isNull();
        assertThat(report.getProcessedAt()).isNull();
    }

    @Test
    void 신고_검토() {
        Report report = ReportFixture.createReport();

        report.review(MEMBER_ID);

        assertThat(report.getStatus()).isEqualTo(ReportStatus.IN_REVIEW);
    }

    @Test
    void 신고_검토_시_대기상태가_아니면_예외가_발생한다() {
        Report report = ReportFixture.createReport();
        report.review(MEMBER_ID);

        assertThatThrownBy(() -> report.review(MEMBER_ID))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("처리 대기 상태가 아닙니다");
    }

    @Test
    void 신고_완료_처리() {
        Report report = ReportFixture.createReport();
        report.review(MEMBER_ID);

        report.process();

        assertThat(report.getStatus()).isEqualTo(ReportStatus.PROCESSED);
    }

    @Test
    void 신고_완료_처리_시_검토_상태가_아니면_예외가_발생한다() {
        Report report = ReportFixture.createReport();
        assertThat(report.getStatus()).isEqualTo(PENDING);

        assertThatThrownBy(report::process)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("검토 상태가 아닙니다");
    }

    @Test
    void 신고_취소_처리() {
        Report report = ReportFixture.createReport();
        report.review(MEMBER_ID);

        report.abort(CLOSED);

        assertThat(report.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    void 신고_중복_처리() {
        Report report = ReportFixture.createReport();
        report.review(MEMBER_ID);

        report.abort(DUPLICATED);

        assertThat(report.getStatus()).isEqualTo(DUPLICATED);
    }

    @Test
    void 신고_취소_처리_시_검토_상태가_아니면_예외가_발생한다() {
        Report report = ReportFixture.createReport();
        assertThat(report.getStatus()).isEqualTo(PENDING);

        assertThatThrownBy(report::process)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("검토 상태가 아닙니다");
    }
}
