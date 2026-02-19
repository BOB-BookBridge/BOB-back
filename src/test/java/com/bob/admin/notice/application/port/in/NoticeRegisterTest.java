package com.bob.admin.notice.application.port.in;

import static com.bob.support.fixture.member.domain.MemberFixture.MANAGER_ID;
import static com.bob.support.fixture.notice.domain.NoticeFixture.createBannerNotice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bob.admin.notice.application.dto.command.RegisterAlertCommand;
import com.bob.admin.notice.application.dto.command.RegisterBannerCommand;
import com.bob.admin.notice.domain.Notice;
import com.bob.admin.notice.domain.NoticeType;
import com.bob.admin.notice.domain.repository.NoticeRepository;
import com.bob.shared.event.NoticeNotificationEvent;
import com.bob.support.annotation.ContainerTest;

@DisplayName("공지 등록 테스트")
@ContainerTest
@RequiredArgsConstructor
class NoticeRegisterTest {

    final NoticeRegister noticeRegister;
    final NoticeRepository noticeRepository;
    final EntityManager em;

    @MockitoBean
    final ApplicationEventPublisher eventPublisher;

    @Test
    void 게시용_공지_등록() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);
        var command = new RegisterBannerCommand(MANAGER_ID, "content", endTime);

        Notice notice = noticeRegister.registerBanner(command);

        assertThat(notice.getId()).isNotNull();
        assertThat(notice.getType()).isEqualTo(NoticeType.BANNER);
        assertThat(notice.getWriterId()).isEqualTo(MANAGER_ID);
        assertThat(notice.getContent()).isEqualTo("content");
        assertThat(notice.getEndsAt()).isEqualTo(endTime);
    }

    @Test
    void 게시용_공지_등록_시_기존공지가_종료된다() {
        Notice first = noticeRepository.save(createBannerNotice());

        var command = new RegisterBannerCommand(MANAGER_ID, "second", LocalDateTime.now().plusHours(5));
        noticeRegister.registerBanner(command);

        em.flush();
        em.clear();

        Notice remained = noticeRepository.findById(first.getId()).orElseThrow();
        assertThat(remained.getEndsAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 알림용_공지_등록() {
        var command = new RegisterAlertCommand(MANAGER_ID, "title", "content");

        Notice notice = noticeRegister.registerAlert(command);

        assertThat(notice.getId()).isNotNull();
        assertThat(notice.getType()).isEqualTo(NoticeType.ALERT);
        assertThat(notice.getWriterId()).isEqualTo(MANAGER_ID);
        assertThat(notice.getTitle()).isEqualTo("[title]");
        assertThat(notice.getContent()).isEqualTo("content");
        assertThat(notice.getEndsAt()).isNull();

        then(eventPublisher).should().publishEvent(any(NoticeNotificationEvent.class));
    }
}
