package com.bob.core.application.notification.port.in;

import static com.bob.global.exception.response.ApplicationError.NOTIFICATION_ACCESS_DENIED;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.domain.NotificationFixture.createNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.notification.dto.command.MarkAsReadCommand;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.support.annotation.ContainerTest;

@DisplayName("알림 수정 테스트")
@ContainerTest
record NotificationMarkerTest(
    NotificationMarker notificationMarker, NotificationRepository notificationRepository, EntityManager em
) {

    @Test
    void 알림_읽음_처리() {
        Notification saved = notificationRepository.save(createNotification("1", MEMBER_ID, "body", false));
        assertThat(saved.getIsRead()).isFalse();

        MarkAsReadCommand command = new MarkAsReadCommand(MEMBER_ID);

        Notification notification = notificationMarker.markAsRead(saved.getId(), command);

        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    void 알림_읽음_처리_시_소유자가_아니면_사용자_예외가_발생한다() {
        Notification saved = notificationRepository.save(createNotification("1", MEMBER_ID, "body", false));

        MarkAsReadCommand command = new MarkAsReadCommand(OTHER_MEMBER_ID);

        assertThatThrownBy(() -> notificationMarker.markAsRead(saved.getId(), command))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(NOTIFICATION_ACCESS_DENIED.getMessage());
    }

    @Test
    void 모든_알림_읽음_처리() {
        Notification noti1 = notificationRepository.save(createNotification(false));
        Notification noti2 = notificationRepository.save(createNotification(false));
        assertThat(noti1.getIsRead()).isFalse();
        assertThat(noti2.getIsRead()).isFalse();

        MarkAsReadCommand command = new MarkAsReadCommand(MEMBER_ID);

        em.flush();
        em.clear();

        List<Notification> notifications = notificationMarker.markAllAsRead(command);

        assertThat(notifications).hasSize(2);
        assertThat(notifications.get(0).getIsRead()).isTrue();
        assertThat(notifications.get(1).getIsRead()).isTrue();
    }
}
