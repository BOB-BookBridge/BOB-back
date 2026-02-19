package com.bob.admin.notice.domain;

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
public class Notice extends AbstractEntity {

    private NoticeType type;

    private UUID writerId;

    private String title;
    private String content;

    private LocalDateTime endsAt;

    private LocalDateTime createdAt;

    public static Notice createBanner(UUID writerId, String content, LocalDateTime endTime) {
        Assert.hasText(content, "내용은 필수입니다");

        Assert.isTrue(endTime.isAfter(LocalDateTime.now()), "게시 종료 시각은 현재 시각 이후여야 합니다");

        return Notice.builder()
            .type(NoticeType.BANNER)
            .writerId(writerId)
            .title("[공지사항]")
            .content(content)
            .endsAt(endTime)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static Notice createAlert(UUID writerId, String title, String content) {
        Assert.hasText(content, "내용은 필수입니다");

        String alertTitle = title == null ? "공지" : title;

        return Notice.builder()
            .type(NoticeType.ALERT)
            .writerId(writerId)
            .title(alertTitle)
            .content(content)
            .endsAt(null)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void deactivate() {
        this.endsAt = LocalDateTime.now();
    }
}
