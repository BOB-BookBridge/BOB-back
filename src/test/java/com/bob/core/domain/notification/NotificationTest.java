package com.bob.core.domain.notification;

import static com.bob.core.domain.notification.NotificationType.CHAT;
import static com.bob.core.domain.notification.NotificationType.TRADE;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("알림 도메인 테스트")
class NotificationTest {

    @Test
    void 알림_생성() {
        String referenceId = "1";
        Notification notification = Notification.createNotification(TRADE, referenceId, OTHER_MEMBER_ID, "body");

        assertThat(notification.getType()).isEqualTo(TRADE);
        assertThat(notification.getReferenceId()).isEqualTo(referenceId);
        assertThat(notification.getReceiverId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    void 알림_읽음_처리() {
        String referenceId = "1";
        Notification notification = Notification.createNotification(TRADE, referenceId, OTHER_MEMBER_ID, "body");

        assertThat(notification.getIsRead()).isFalse();

        notification.markAsRead();

        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    void 알림_구분() {
        Notification chatNotification = Notification.createNotification(CHAT, "1", OTHER_MEMBER_ID, "채팅");
        Notification tradeNotification = Notification.createNotification(TRADE, "2", OTHER_MEMBER_ID, "거래");

        assertThat(chatNotification.isChatNotification()).isTrue();
        assertThat(tradeNotification.isChatNotification()).isFalse();
    }
}
