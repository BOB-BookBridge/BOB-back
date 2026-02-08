package com.bob.support.fixture.report.domain;

import static com.bob.core.report.domain.ReportTarget.CHAT;
import static com.bob.core.report.domain.ReportTarget.POST;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;

import java.util.UUID;

import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;

public class ReportFixture {

    public static Report createReport(UUID reporterId, UUID reportedId) {
        return Report.createReport(POST, 1L, "사기/허위", reporterId, reportedId);
    }

    public static Report createReport(Long targetId) {
        return Report.createReport(POST, targetId, "사기/허위", MEMBER_ID, OTHER_MEMBER_ID);
    }

    public static Report createReport() {
        return createReport(MEMBER_ID, OTHER_MEMBER_ID);
    }

    public static Report createChatReport(Long chatMessageId, UUID reporterId, UUID reportedId) {
        return Report.createReport(CHAT, chatMessageId, "욕설/비방", reporterId, reportedId);
    }

    public static Report createChatReport(Long chatMessageId) {
        return createChatReport(chatMessageId, MEMBER_ID, OTHER_MEMBER_ID);
    }

    public static Report createInReviewReport() {
        Report report = createReport();
        report.review(MANAGER_ID);

        return report;
    }

    public static Report createProcessedReport() {
        Report report = createReport();
        report.review(MANAGER_ID);
        report.process();

        return report;
    }

    public static Report createProcessedReport(Long targetId) {
        Report report = createReport(targetId);
        report.review(MANAGER_ID);
        report.process();

        return report;
    }

    public static Report createDuplicatedReport() {
        Report report = createReport();
        report.review(MANAGER_ID);
        report.abort(ReportStatus.DUPLICATED);

        return report;
    }

    public static Report createClosedReport() {
        Report report = createReport();
        report.review(MANAGER_ID);
        report.abort(ReportStatus.CLOSED);

        return report;
    }
}
