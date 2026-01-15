package com.bob.admin.report.application.port.in;

import static com.bob.core.chat.domain.type.ChatMessageType.TEXT;
import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageRequest;

import com.bob.admin.report.application.dto.query.ReadManagementReportsQuery;
import com.bob.admin.report.application.port.result.ManagementReportDetail;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.admin.report.application.port.result.ReportedChatContent;
import com.bob.admin.report.application.port.result.ReportedPostContent;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.ChatroomRepository;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.repository.ReportRepository;
import com.bob.support.annotation.ContainerTest;
import com.bob.support.fixture.chat.domain.ChatroomFixture;
import com.bob.support.fixture.report.domain.ReportFixture;

@DisplayName("관리자 - 신고 조회 테스트")
@ContainerTest
record ManagementReportReaderTest(
    ManagementReportReader reportReader, ReportRepository reportRepository,
    ChatroomRepository chatroomRepository
) {

    @Test
    void 신고_목록_조회() {
        Report report1 = reportRepository.save(ReportFixture.createReport());
        Report report2 = reportRepository.save(ReportFixture.createProcessedReport());
        Report report3 = reportRepository.save(ReportFixture.createDuplicatedReport());

        var query = ReadManagementReportsQuery.of(null, null, null, null);
        var pageable = PageRequest.of(0, 20);

        ManagementReportSummaries result = reportReader.readAll(query, pageable);

        assertThat(result.totalCount()).isGreaterThanOrEqualTo(3);
        assertThat(result.reports()).isNotEmpty();
        assertThat(result.reports())
            .allSatisfy(report -> {
                assertThat(report.reporter()).isNotNull();
                assertThat(report.reporter().id()).isNotNull();
                assertThat(report.reporter().email()).isNotBlank();
                assertThat(report.reporter().nickname()).isNotBlank();
                assertThat(report.reported()).isNotNull();
                assertThat(report.reported().id()).isNotNull();
                assertThat(report.reported().email()).isNotBlank();
                assertThat(report.reported().nickname()).isNotBlank();
            });
    }

    @Test
    void 신고_목록_조회_신고자_이메일_필터링() {
        Report report = reportRepository.save(ReportFixture.createReport(MEMBER_ID, OTHER_MEMBER_ID));

        var query = ReadManagementReportsQuery.of("test@test.com", null, null, null);
        var pageable = PageRequest.of(0, 20);

        ManagementReportSummaries result = reportReader.readAll(query, pageable);

        assertThat(result.reports()).isNotEmpty();
        assertThat(result.reports())
            .allSatisfy(r -> assertThat(r.reporter().email()).contains("test@test.com"));
    }

    @Test
    void 신고_목록_조회_피신고자_이메일_필터링() {
        Report report = reportRepository.save(ReportFixture.createReport(MEMBER_ID, OTHER_MEMBER_ID));

        var query = ReadManagementReportsQuery.of(null, "other@test.com", null, null);
        var pageable = PageRequest.of(0, 20);

        ManagementReportSummaries result = reportReader.readAll(query, pageable);

        assertThat(result.reports()).isNotEmpty();
        assertThat(result.reports())
            .allSatisfy(r -> assertThat(r.reported().email()).contains("other@test.com"));
    }

    @Test
    void 신고_목록_조회_타입_필터링() {
        Report report = reportRepository.save(ReportFixture.createReport());

        var query = ReadManagementReportsQuery.of(null, null, "POST", null);
        var pageable = PageRequest.of(0, 20);

        ManagementReportSummaries result = reportReader.readAll(query, pageable);

        assertThat(result.reports()).isNotEmpty();
        assertThat(result.reports())
            .allSatisfy(r -> assertThat(r.type()).isEqualTo("POST"));
    }

    @Test
    void 신고_목록_조회_상태_필터링() {
        Report report = reportRepository.save(ReportFixture.createProcessedReport());

        var query = ReadManagementReportsQuery.of(null, null, null, "PROCESSED");
        var pageable = PageRequest.of(0, 20);

        ManagementReportSummaries result = reportReader.readAll(query, pageable);

        assertThat(result.reports()).isNotEmpty();
        assertThat(result.reports())
            .allSatisfy(r -> assertThat(r.status()).isEqualTo("PROCESSED"));
    }

    @Test
    void 게시글_신고_상세_조회() {
        Report report = reportRepository.save(ReportFixture.createReport());

        ManagementReportDetail result = reportReader.readDetail(report.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(report.getId());
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.type()).isEqualTo("POST");
        assertThat(result.reason()).isEqualTo("사기/허위");
        assertThat(result.managerNickname()).isNull();

        // 신고자 정보
        assertThat(result.reporter()).isNotNull();
        assertThat(result.reporter().id()).isEqualTo(MEMBER_ID);
        assertThat(result.reporter().email()).isNotBlank();
        assertThat(result.reporter().nickname()).isNotBlank();

        // 피신고자 정보
        assertThat(result.reported()).isNotNull();
        assertThat(result.reported().id()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(result.reported().email()).isNotBlank();
        assertThat(result.reported().nickname()).isNotBlank();

        // 신고된 컨텐츠 : 게시글
        assertThat(result.reportedContent()).isNotNull();
        assertThat(result.reportedContent()).isInstanceOf(ReportedPostContent.class);

        ReportedPostContent content = (ReportedPostContent)result.reportedContent();
        assertThat(content.id()).isNotNull();
    }

    @Test
    void 채팅_신고_상세_조회() {
        Long reportedMessageId = getReportedMessageId();

        Report chatReport = ReportFixture.createChatReport(reportedMessageId);
        chatReport.review(MANAGER_ID);
        chatReport.process();
        reportRepository.save(chatReport);

        ManagementReportDetail result = reportReader.readDetail(chatReport.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(chatReport.getId());
        assertThat(result.status()).isEqualTo("PROCESSED");
        assertThat(result.type()).isEqualTo("CHAT");
        assertThat(result.reason()).isEqualTo("욕설/비방");
        assertThat(result.managerNickname()).isEqualTo("manager");

        // 신고된 컨텐츠 : 채팅 (피신고자의 채팅만 포함)
        assertThat(result.reportedContent()).isNotNull();
        assertThat(result.reportedContent()).isInstanceOf(ReportedChatContent.class);

        ReportedChatContent chatContent = (ReportedChatContent)result.reportedContent();
        assertThat(chatContent.messages()).isNotEmpty();
        assertThat(chatContent.messages()).hasSize(3);
        assertThat(chatContent.messages())
            .allSatisfy(msg -> {
                assertThat(msg.content()).isNotBlank();
                assertThat(msg.sentAt()).isNotNull();
            });
    }

    private @Nullable Long getReportedMessageId() {
        Chatroom chatroom = ChatroomFixture.createChatroom();

        // 피신고자의 채팅, size = 3
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지1", TEXT);
        chatroom.addMessage(OTHER_MEMBER_ID, "메시지2", TEXT);
        ChatMessage reported = chatroom.addMessage(OTHER_MEMBER_ID, "신고된 메시지", TEXT);

        // 신고자의 채팅, size = 1
        chatroom.addMessage(MEMBER_ID, "신고자 메시지", TEXT);

        chatroomRepository.save(chatroom);

        return reported.getId();
    }
}
