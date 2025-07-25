package com.bob.support.fixture.domain.noti;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.notification.entity.Notification;
import com.bob.domain.notification.entity.NotificationType;
import java.util.List;
import java.util.UUID;

public class NotificationFixture {

  public static List<Notification> DEFAULT_NOTIFICATIONS() {
    return List.of(DEFAULT_NOTIFICATION_1(), DEFAULT_NOTIFICATION_2());
  }

  public static Notification DEFAULT_NOTIFICATION_1() {
    return Notification.builder()
        .id(1L)
        .type(NotificationType.TRADE)
        .receiverId(MEMBER_ID)
        .body("body")
        .isRead(false)
        .build();
  }

  public static Notification DEFAULT_NOTIFICATION_2() {
    return Notification.builder()
        .id(2L)
        .type(NotificationType.TRADE)
        .receiverId(MEMBER_ID)
        .body("body")
        .isRead(true)
        .build();
  }

  public static Notification CUSTOM_NOTIFICATION(String refId, UUID receiverId, String body, boolean isRead) {
    return Notification.builder()
        .type(NotificationType.TRADE)
        .referenceId(refId)
        .receiverId(receiverId)
        .body(body)
        .isRead(isRead)
        .build();
  }
}
