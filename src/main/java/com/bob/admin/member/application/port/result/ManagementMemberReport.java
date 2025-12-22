package com.bob.admin.member.application.port.result;

import java.util.List;
import java.util.Set;

public record ManagementMemberReport(ChatReport chat, PostReport post) {

    public static ManagementMemberReport of(
        int chatCount, Set<String> chatReportReason, List<Long> chatIds,
        int postCount, Set<String> postReportReason, List<Long> postIds
    ) {
        return new ManagementMemberReport(
            new ChatReport(chatCount, chatReportReason, chatIds),
            new PostReport(postCount, postReportReason, postIds)
        );
    }

    public record ChatReport(Integer count, Set<String> reason, List<Long> references) {

    }

    public record PostReport(Integer count, Set<String> reason, List<Long> references) {

    }
}
