package com.bob.core.report.adapter.out;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.bob.admin.report.application.port.out.ManagementReportPort;
import com.bob.admin.report.application.port.result.ManagementReport;
import com.bob.admin.report.application.port.result.ManagementReportDetail;
import com.bob.admin.report.application.port.result.ManagementReportSummaries;
import com.bob.admin.report.application.port.result.ReportMemberInfo;
import com.bob.admin.report.application.port.result.ReportedChatContent;
import com.bob.admin.report.application.port.result.ReportedChatMessageInfo;
import com.bob.admin.report.application.port.result.ReportedContent;
import com.bob.admin.report.application.port.result.ReportedMemberInfo;
import com.bob.admin.report.application.port.result.ReportedPostContent;
import com.bob.core.chat.application.port.in.ChatroomReader;
import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.member.application.port.in.MemberReader;
import com.bob.core.post.application.port.in.PostReader;
import com.bob.core.post.domain.Post;
import com.bob.core.report.application.dto.command.ChangeReportStatusCommand;
import com.bob.core.report.application.dto.query.ReadReportCountQuery;
import com.bob.core.report.application.dto.result.ReportSummaries;
import com.bob.core.report.application.port.in.ReportModifier;
import com.bob.core.report.application.port.in.ReportReader;
import com.bob.core.report.application.port.in.ReportSearcher;
import com.bob.core.report.domain.Report;
import com.bob.core.report.domain.ReportStatus;
import com.bob.core.report.domain.ReportTarget;
import com.bob.core.report.domain.repository.dsl.query.SearchReportsQuery;
import com.bob.core.report.domain.repository.projection.ReportCount;

@Component
@RequiredArgsConstructor
public class ManagementReportAdapter implements ManagementReportPort {

    private final ReportSearcher reportSearcher;
    private final ReportReader reportReader;
    private final ReportModifier reportModifier;

    private final MemberReader memberReader;
    private final PostReader postReader;
    private final ChatroomReader chatroomReader;

    @Override
    public ManagementReportSummaries readAll(
        String reporterEmail, String reportedEmail, String type, String status, Pageable pageable
    ) {
        SearchReportsQuery query = buildQuery(reporterEmail, reportedEmail, type, status);

        ReportSummaries summaries = reportSearcher.searchByQuery(query, pageable);

        List<ManagementReport> managementReports = summaries.reports().stream().map(this::convert).toList();

        return new ManagementReportSummaries(summaries.totalCount(), managementReports);
    }

    @Override
    public ManagementReportDetail readDetail(Long reportId) {
        Report report = reportReader.read(reportId);

        String reporterEmail = getEmail(report.getReporterId());
        String reportedEmail = getEmail(report.getReportedId());
        String reporterNickname = getNickname(report.getReporterId());
        String reportedNickname = getNickname(report.getReportedId());
        String managerNickname = report.getManagerId() != null ? getNickname(report.getManagerId()) : null;

        ReportMemberInfo reporter = new ReportMemberInfo(report.getReporterId(), reporterEmail, reporterNickname);
        ReportMemberInfo reported = new ReportMemberInfo(report.getReportedId(), reportedEmail, reportedNickname);

        ReportedContent reportedContent = null;

        switch (report.getTarget()) {
            case POST -> reportedContent = readPostContent(report.getTargetId());
            case CHAT -> reportedContent = readChatContent(report.getTargetId(), report.getReportedId());
        }

        ReadReportCountQuery countQuery = new ReadReportCountQuery(List.of(report.getReportedId()));
        int reportedProcessedCount = reportReader.readProcessedReportCounts(countQuery)
            .stream()
            .filter(count -> count.getReportedId().equals(report.getReportedId()))
            .map(ReportCount::getCount)
            .findFirst()
            .orElse(0);

        return ManagementReportDetail.builder()
            .id(report.getId())
            .status(report.getStatus().name())
            .type(report.getTarget().name())
            .reason(report.getReason())
            .createdAt(report.getCreatedAt())
            .processedAt(report.getProcessedAt())
            .reporter(reporter)
            .reported(reported)
            .managerNickname(managerNickname)
            .reportedContent(reportedContent)
            .reportedProcessedCount(reportedProcessedCount)
            .build();
    }

    @Override
    public ReportedMemberInfo changeStatus(Long reportId, UUID managerId, String status) {
        ChangeReportStatusCommand command = new ChangeReportStatusCommand(managerId, status);
        Report report = reportModifier.changeStatus(reportId, command);

        ReadReportCountQuery query = new ReadReportCountQuery(List.of(report.getReportedId()));
        int count = reportReader.readProcessedReportCounts(query).stream()
            .findFirst().map(ReportCount::getCount)
            .orElse(0);

        return new ReportedMemberInfo(report.getReportedId(), count);
    }

    private SearchReportsQuery buildQuery(String reporterEmail, String reportedEmail, String type, String status) {
        UUID reporterId = reporterEmail != null ? getId(reporterEmail) : null;
        UUID reportedId = reportedEmail != null ? getId(reportedEmail) : null;
        ReportTarget target = type != null ? ReportTarget.valueOf(type) : null;
        ReportStatus reportStatus = status != null ? ReportStatus.valueOf(status) : null;

        return new SearchReportsQuery(reporterId, reportedId, target, reportStatus);
    }

    private ManagementReport convert(Report report) {
        String reporterEmail = getEmail(report.getReporterId());
        String reportedEmail = getEmail(report.getReportedId());
        String reporterNickname = getNickname(report.getReporterId());
        String reportedNickname = getNickname(report.getReportedId());
        String managerNickname = report.getManagerId() != null ? getNickname(report.getManagerId()) : null;

        ReportMemberInfo reporter = new ReportMemberInfo(report.getReporterId(), reporterEmail, reporterNickname);
        ReportMemberInfo reported = new ReportMemberInfo(report.getReportedId(), reportedEmail, reportedNickname);

        return ManagementReport.builder()
            .id(report.getId())
            .status(report.getStatus().name())
            .type(report.getTarget().name())
            .reason(report.getReason())
            .reporter(reporter)
            .reported(reported)
            .createdAt(report.getCreatedAt())
            .processedAt(report.getProcessedAt())
            .managerNickname(managerNickname)
            .build();
    }

    private ReportedPostContent readPostContent(Long postId) {
        Post post = postReader.read(postId);

        return new ReportedPostContent(post.getId(), post.getTitle(), post.getDescription(), post.getThumbnailUrl());
    }

    private ReportedChatContent readChatContent(Long chatMessageId, UUID reportedUserId) {
        Chatroom chatroom = chatroomReader.readByMessageId(chatMessageId);

        List<ChatMessage> allMessages = chatroom.getMessages();
        int totalMessages = allMessages.size();
        int startIndex = Math.max(0, totalMessages - 30);

        List<ReportedChatMessageInfo> recentMessages = allMessages.subList(startIndex, totalMessages)
            .stream()
            .filter(message -> message.getSenderId().equals(reportedUserId))
            .map(message -> new ReportedChatMessageInfo(message.getContent(), message.getCreatedAt()))
            .toList();

        return new ReportedChatContent(recentMessages);
    }

    private UUID getId(String email) {
        return memberReader.read(email).getId();
    }

    private String getEmail(UUID memberId) {
        return memberReader.read(memberId).getEmail();
    }

    private String getNickname(UUID memberId) {
        return memberReader.read(memberId).getNickname();
    }
}
