package com.bob.admin.post.application.port.result;

import java.time.LocalDateTime;

import lombok.Builder;

import com.bob.admin.post.domain.PostManagementHistory;

@Builder
public record ManagementPostDetail(
    Long id,
    String status,
    String title,
    String thumbnailUrl,
    String description,
    LocalDateTime createdAt,
    ManagementPostWriter writer,
    ManagementPostReports reports,
    String managerNickname,
    String previousStatus,
    String memo,
    LocalDateTime processedAt
) {

    public static ManagementPostDetail of(ManagementPost post, ManagementPostReports reports,
        PostManagementHistory history, String managerNickname) {
        return ManagementPostDetail.builder()
            .id(post.id())
            .status(post.status())
            .title(post.title())
            .thumbnailUrl(post.thumbnailUrl())
            .description(post.description())
            .createdAt(post.createdAt())
            .writer(post.writer())
            .reports(reports)
            .managerNickname(managerNickname)
            .previousStatus(history != null && history.getPreviousStatus() != null
                ? history.getPreviousStatus().name()
                : null
            )
            .memo(history != null ? history.getMemo() : null)
            .processedAt(history != null ? history.getProcessedAt() : null)
            .build();
    }
}
