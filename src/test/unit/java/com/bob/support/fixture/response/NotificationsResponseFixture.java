package com.bob.support.fixture.response;

import com.bob.domain.notification.service.dto.response.NotificationsResponse;
import com.bob.domain.notification.service.dto.response.internal.NotificationSummary;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationsResponseFixture {

  public static NotificationsResponse FILTERING_NOTIFICATIONS_RESPONSE() {
    return new NotificationsResponse(List.of(RECENT_NOTI_SUMMARY_1(), RECENT_NOTI_SUMMARY_2()));
  }

  public static NotificationSummary RECENT_NOTI_SUMMARY_1() {
    return NotificationSummary.builder()
        .id(3L)
        .type("TRADE")
        .refId("1")
        .body(convertBody("책1", "완료"))
        .isRead(false)
        .createdAt(LocalDateTime.now())
        .build();
  }

  public static NotificationSummary RECENT_NOTI_SUMMARY_2() {
    return NotificationSummary.builder()
        .id(2L)
        .type("TRADE")
        .refId("2")
        .body(convertBody("책2", "완료"))
        .isRead(true)
        .createdAt(LocalDateTime.now())
        .build();
  }

  private static String convertBody(String title, String status) {
    return String.format("[%s]의 거래 상태가 '%s'(으)로 변경되었습니다.", title, status);
  }
}
