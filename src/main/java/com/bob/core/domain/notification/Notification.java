package com.bob.core.domain.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends AbstractEntity {

    private NotificationType type;

    private String referenceId;

    private UUID receiverId;

    private String body;

    @Builder.Default
    private Boolean isRead = false;

    private LocalDateTime createdAt;

    public static Notification createNotification(NotificationType type, String refId, UUID receiverId, String body) {
        return Notification.builder()
            .referenceId(refId)
            .type(type)
            .receiverId(receiverId)
            .body(body)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isChatNotification() {
        return type == NotificationType.CHAT;
    }
}
