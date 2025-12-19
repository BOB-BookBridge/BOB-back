package com.bob.core.application.management.port.result;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagementMember {

    UUID id;
    String status;
    String role;
    String email;
    String nickname;
    Integer reportCount;
    LocalDateTime lastActiveAt;
    LocalDateTime createdAt;

    public void updateReportCount(Integer count) {
        this.reportCount = count;
    }
}
