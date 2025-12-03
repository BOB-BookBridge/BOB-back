package com.bob.core.application.notification.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.notification.domain.NotificationFixture.createNotification;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bob.core.application.notification.dto.query.ReadByMemberQuery;
import com.bob.core.domain.notification.Notification;
import com.bob.core.domain.notification.repository.NotificationRepository;
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
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("알림을 찾을 수 없습니다.");
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
    void 알림_목록_조회_1달_이내() {
        Notification oldNotification = createNotification(MEMBER_ID, now().minusMonths(1).minusDays(1));
        notificationRepository.save(oldNotification);

        notificationRepository.save(createNotification("recent", MEMBER_ID, "최근 알림", false));

        ReadByMemberQuery query = new ReadByMemberQuery(MEMBER_ID);

        List<Notification> notifications = notificationReader.readByMember(query);

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getBody()).isEqualTo("최근 알림");

        LocalDateTime oneMonth = now().minusMonths(1);

        assertThat(notifications)
            .allSatisfy(notification -> assertThat(notification.getCreatedAt()).isAfter(oneMonth));
    }

    @Test
    void 알림_목록_조회_최신순() {
        Notification current = notificationRepository.save(createNotification(MEMBER_ID, now().minusDays(1)));
        Notification old = notificationRepository.save(createNotification(MEMBER_ID, now().minusDays(2)));

        List<Notification> notifications = notificationReader.readByMember(new ReadByMemberQuery(MEMBER_ID));

        assertThat(notifications.get(0).getId()).isEqualTo(current.getId());
        assertThat(notifications.get(1).getId()).isEqualTo(old.getId());
    }
}
