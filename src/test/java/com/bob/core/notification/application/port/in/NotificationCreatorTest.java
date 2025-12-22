package com.bob.core.notification.application.port.in;

import static com.bob.core.notification.domain.NotificationType.CHAT;
import static com.bob.core.notification.domain.NotificationType.TRADE;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.dto.command.CreateNotificationCommandFixture.createCreateNotificationCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.core.notification.application.dto.command.CreateNotificationCommand;
import com.bob.core.notification.application.port.out.NotificationMessagePort;
import com.bob.core.notification.domain.Notification;
import com.bob.core.notification.domain.repository.NotificationRepository;
import com.bob.support.annotation.ContainerTest;

@DisplayName("알림 생성 테스트")
@ContainerTest
class NotificationCreatorTest {

    @Autowired
    NotificationCreator notificationCreator;

    @Autowired
    NotificationRepository notificationRepository;

    @MockitoBean
    NotificationMessagePort messagePort;

    @Test
    void 알림_생성() {
        CreateNotificationCommand command = createCreateNotificationCommand(TRADE, MEMBER_ID, OTHER_MEMBER_ID);

        Notification notification = notificationCreator.create(command);

        assertThat(notification).isNotNull();
        assertThat(notification.getId()).isNotNull();
        assertThat(notification.getType()).isEqualTo(TRADE);

        then(messagePort).should()
            .publish(OTHER_MEMBER_ID, "TRADE", "1", "1", "body", List.of(), false, false, MEMBER_ID, "tester", null);
    }

    @Test
    void 알림_생성_시_채팅_알림_이라면_저장되지_않는다() {
        CreateNotificationCommand command = createCreateNotificationCommand(CHAT, MEMBER_ID, OTHER_MEMBER_ID);

        long countBefore = notificationRepository.count();

        Notification notification = notificationCreator.create(command);

        assertThat(notification).isNotNull();
        assertThat(notification.getType()).isEqualTo(CHAT);
        assertThat(notification.isChatNotification()).isTrue();

        assertThat(notificationRepository.count()).isEqualTo(countBefore);

        then(messagePort).should()
            .publish(OTHER_MEMBER_ID, "CHAT", "1", "1", "body", List.of(), false, false, MEMBER_ID, "tester", null);
    }
}
