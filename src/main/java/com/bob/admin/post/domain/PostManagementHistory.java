package com.bob.admin.post.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.shared.entity.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostManagementHistory extends AbstractEntity {

    private Long postId;
    private UUID managerId;
    private ManagementStatus previousStatus;
    private ManagementStatus status;
    private String memo;
    private LocalDateTime processedAt;

    public static PostManagementHistory create(Long postId, UUID managerId, ManagementStatus previousStatus,
        ManagementStatus status, String memo
    ) {
        return PostManagementHistory.builder()
            .postId(postId)
            .managerId(managerId)
            .previousStatus(previousStatus)
            .status(status)
            .memo(memo)
            .processedAt(LocalDateTime.now())
            .build();
    }
}
