package com.bob.support.fixture.report.domain;

import static com.bob.core.domain.report.ReportTarget.POST;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.core.domain.report.Report;

public class ReportFixture {

    public static Report createReport() {
        return Report.createReport(POST, 1L, "사기/허위", MEMBER_ID, OTHER_MEMBER_ID);
    }

    public static Report createProcessedReport() {
        Report report = createReport();
        report.review(MANAGER_ID);
        report.process();

        return report;
    }
}
