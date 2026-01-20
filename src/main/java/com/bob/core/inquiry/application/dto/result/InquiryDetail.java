package com.bob.core.inquiry.application.dto.result;

import java.time.LocalDateTime;

import lombok.Builder;

import com.bob.core.inquiry.domain.Inquiry;

@Builder
public record InquiryDetail(
    Long id, String email, String title, String content, String reply,
    String status, String managerNickname, LocalDateTime processedAt, LocalDateTime createdAt
) {

    public static InquiryDetail of(Inquiry inquiry, String managerNickname) {
        return InquiryDetail.builder()
            .id(inquiry.getId())
            .email(inquiry.getEmail())
            .title(inquiry.getTitle())
            .content(inquiry.getContent())
            .reply(inquiry.getReply())
            .status(inquiry.getStatus().name())
            .managerNickname(managerNickname)
            .processedAt(inquiry.getProcessedAt())
            .createdAt(inquiry.getCreatedAt())
            .build();
    }
}
