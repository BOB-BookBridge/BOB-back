package com.bob.admin.inquiry.application.port.result;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ManagementInquiry(
    Long id,
    String status,
    String title,
    String email,
    String managerNickname,
    LocalDateTime createdAt,
    LocalDateTime processedAt
) {

}
