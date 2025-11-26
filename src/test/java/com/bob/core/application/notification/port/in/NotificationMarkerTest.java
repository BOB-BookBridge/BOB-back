package com.bob.core.application.notification.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.domain.NotificationFixture.createNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.notification.dto.command.MarkAsReadCommand;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.annotation.ContainerTest;

@DisplayName("알림 수정 테스트")
@ContainerTest
record NotificationMarkerTest(NotificationMarker notificationMarker, NotificationRepository notificationRepository) {

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
            .hasMessage(ApplicationError.NOTIFICATION_ACCESS_DENIED.getMessage());
    }
}
