package com.bob.core.inquiry.domain;

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
public class Inquiry extends AbstractEntity {

    private InquiryStatus status;

    private String email;
    private String title;
    private String content;

    private UUID managerId;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static Inquiry createInquiry(String email, String title, String content) {
        return Inquiry.builder()
            .status(InquiryStatus.PENDING)
            .email(email)
            .title(title)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void review(UUID managerId) {
        Assert.state(status == InquiryStatus.PENDING, "처리 대기 상태가 아닙니다");

        this.managerId = managerId;
        this.status = InquiryStatus.IN_REVIEW;
    }

    public void process() {
        Assert.state(status == InquiryStatus.IN_REVIEW, "검토 상태가 아닙니다");

        this.status = InquiryStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    public void close(InquiryStatus status) {
        Assert.state(this.status == InquiryStatus.IN_REVIEW, "검토 상태가 아닙니다");

        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}
