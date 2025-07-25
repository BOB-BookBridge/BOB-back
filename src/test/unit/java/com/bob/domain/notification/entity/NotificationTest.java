package com.bob.domain.notification.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("알림 도메인 테스트")
class NotificationTest {

  @Test
  @DisplayName("읽음 상태 변경 테스트")
  void 읽음_상태를_변경할_수_있다() {
    // given
    Notification notification = Notification.builder()
        .type(NotificationType.TRADE)
        .referenceId("1")
        .receiverId(UUID.randomUUID())
        .body("알림 본문")
        .isRead(false)
        .build();

    // when
    notification.updateReadStatus(true);

    // then
    assertThat(notification.getIsRead()).isTrue();
  }
}