package com.bob.core.application.notification.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.domain.NotificationFixture.createNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.annotation.ContainerTest;

@DisplayName("알림 조회 테스트")
@ContainerTest
record NotificationReaderTest(NotificationReader notificationReader, NotificationRepository notificationRepository) {

    @Test
    void 알림_조회() {
        Notification saved = notificationRepository.save(createNotification("1", MEMBER_ID, "body", false));

        Notification notification = notificationReader.read(saved.getId());

        assertThat(notification).isNotNull();
        assertThat(notification.getId()).isEqualTo(saved.getId());
        assertThat(notification.getReceiverId()).isEqualTo(MEMBER_ID);
        assertThat(notification.getBody()).isEqualTo("body");
    }

    @Test
    void 알림_조회_시_존재하지_않으면_예외가_발생한다() {
        Long nonExistentId = 999L;

        assertThatThrownBy(() -> notificationReader.read(nonExistentId))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ApplicationError.NOT_EXISTS_NOTIFICATION.getMessage());
    }

    @Test
    void 알림_목록_조회() {
        notificationRepository.save(createNotification("1", MEMBER_ID, "알림1", false));
        notificationRepository.save(createNotification("2", MEMBER_ID, "알림2", false));
        notificationRepository.save(createNotification("3", OTHER_MEMBER_ID, "타인 알림", false));

        ReadByMemberQuery query = new ReadByMemberQuery(MEMBER_ID);

        List<Notification> notifications = notificationReader.readByMember(query);

        assertThat(notifications).hasSize(2);
        assertThat(notifications).extracting("receiverId").containsOnly(MEMBER_ID);
        assertThat(notifications).extracting("body").containsExactlyInAnyOrder("알림1", "알림2");
    }

    @Test
    void 회원별_알림_목록_조회_2주_이내() {
        Notification oldNotification = createNotification(MEMBER_ID, LocalDateTime.now().minusWeeks(2).minusDays(1));
        notificationRepository.save(createNotification("recent", MEMBER_ID, "최근 알림", false));
        notificationRepository.save(oldNotification);

        ReadByMemberQuery query = new ReadByMemberQuery(MEMBER_ID);

        List<Notification> notifications = notificationReader.readByMember(query);

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getBody()).isEqualTo("최근 알림");

        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        assertThat(notifications)
            .allSatisfy(notification -> assertThat(notification.getCreatedAt()).isAfter(twoWeeksAgo));
    }
}
