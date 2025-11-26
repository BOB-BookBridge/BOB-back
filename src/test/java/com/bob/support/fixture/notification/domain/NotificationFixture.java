package com.bob.support.fixture.notification.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.NotificationType;

public class NotificationFixture {

    public static Notification createNotification(String refId, UUID receiverId, String body, boolean isRead,
        LocalDateTime time
    ) {
        return Notification.builder()
            .type(NotificationType.TRADE)
            .referenceId(refId)
            .receiverId(receiverId)
            .body(body)
            .isRead(isRead)
            .createdAt(time)
            .build();
    }

    public static Notification createNotification(String refId, UUID receiverId, String body, boolean isRead) {
        return createNotification(refId, receiverId, body, isRead, LocalDateTime.now());
    }

    public static Notification createNotification(UUID receiverId, LocalDateTime time) {
        return createNotification("1", receiverId, "body", false, time);
    }
}
