package com.bob.core.report.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.Assert;

import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Report extends AbstractEntity {

    private ReportStatus status;
    private ReportTarget target;
    private Long targetId;
    private String reason;

    private UUID reporterId;
    private UUID reportedId;
    private UUID managerId;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static Report createReport(ReportTarget target, Long targetId, String reason, UUID reporterId,
        UUID reportedId) {
        return Report.builder()
            .status(ReportStatus.PENDING)
            .target(target)
            .targetId(targetId)
            .reason(reason)
            .reporterId(reporterId)
            .reportedId(reportedId)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Report createProcessedReport(ReportTarget target, Long targetId, String reason, UUID managerId,
        UUID reportedId) {
        return Report.builder()
            .status(ReportStatus.PROCESSED)
            .target(target)
            .targetId(targetId)
            .reason(reason)
            .reporterId(managerId)
            .reportedId(reportedId)
            .managerId(managerId)
            .processedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void review(UUID managerId) {
        Assert.state(status == ReportStatus.PENDING, "처리 대기 상태가 아닙니다");

        this.managerId = managerId;
        this.status = ReportStatus.IN_REVIEW;
    }

    public void process() {
        Assert.state(status == ReportStatus.IN_REVIEW, "검토 상태가 아닙니다");

        this.status = ReportStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    public void abort(ReportStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
